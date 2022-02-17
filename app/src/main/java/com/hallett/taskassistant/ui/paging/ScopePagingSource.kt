package com.hallett.taskassistant.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hallett.scopes.model.Scope
import com.hallett.scopes.scope_evaluator.IScopeEvaluator
import com.hallett.scopes.scope_generator.IScopeGenerator

class ScopePagingSource(
    private val scopeGenerator: IScopeGenerator,
    private val scopeEvaluator: IScopeEvaluator,
    val includePastScopes: Boolean = false
) : PagingSource<Scope, Scope>() {
    override fun getRefreshKey(state: PagingState<Scope, Scope>): Scope? =
        when (val anchor = state.anchorPosition) {
            null -> null
            else -> state.closestItemToPosition(anchor)
        }

    override suspend fun load(params: LoadParams<Scope>): LoadResult<Scope, Scope> = when (params) {
        is LoadParams.Refresh ->
            generateItems(params.key ?: scopeGenerator.generateScope(), params.loadSize)
        is LoadParams.Append ->
            generateItems(params.key, params.loadSize)
        is LoadParams.Prepend ->
            generateItems(params.key, -params.loadSize)
    }

    private fun generateItems(scope: Scope, number: Int): LoadResult<Scope, Scope> {
        val items = when {
            number < 0 -> mutableListOf<Scope>().apply {
                for (offset in (number + 1)..0) {
                    add(scopeGenerator.add(scope, offset))
                }
            }
            else -> mutableListOf<Scope>().apply {
                for (offset in 0 until number) { // 0 .. (number - 1)
                    add(scopeGenerator.add(scope, offset))
                }
            }
        }.filter { scopeEvaluator.isCurrentOrFutureScope(it) }

        val firstItem = items.firstOrNull()
        val prevKey = when {
            firstItem == null -> null
            scopeEvaluator.isCurrentScope(firstItem) -> null
            else -> scopeGenerator.add(firstItem, -1)
        }

        val nextKey = when (val lastItem = items.lastOrNull()) {
            null -> null
            else -> scopeGenerator.add(lastItem, 1)
        }

        return LoadResult.Page(
            data = items,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}