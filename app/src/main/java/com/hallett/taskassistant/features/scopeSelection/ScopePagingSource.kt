package com.hallett.taskassistant.features.scopeSelection

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hallett.logging.logI
import com.hallett.scopes.model.Scope
import com.hallett.scopes.scope_generator.IScopeCalculator

class ScopePagingSource(
    private val scopeCalculator: IScopeCalculator,
    val includePastScopes: Boolean = false
) : PagingSource<Scope, Scope>() {
    override fun getRefreshKey(state: PagingState<Scope, Scope>): Scope? =
        when (val anchor = state.anchorPosition) {
            null -> null
            else -> state.closestItemToPosition(anchor)
        }

    override suspend fun load(params: LoadParams<Scope>): LoadResult<Scope, Scope> = when (params) {
        is LoadParams.Refresh ->
            generateItems(params.key ?: scopeCalculator.generateScope(), params.loadSize)
        is LoadParams.Append ->
            generateItems(params.key, params.loadSize)
        is LoadParams.Prepend ->
            generateItems(params.key, -params.loadSize)
    }

    private fun generateItems(scope: Scope, number: Int): LoadResult<Scope, Scope> {
        logI("Requesting $number items starting at $scope")
        val items = when {
            number < 0 -> mutableListOf<Scope>().apply {
                // from most negative number to zero
                for (offset in (number + 1)..0) {
                    add(scopeCalculator.add(scope, offset))
                }
            }
            else -> mutableListOf<Scope>().apply {
                // from zero to most positive number
                for (offset in 0 until number) { // 0 .. (number - 1)
                    add(scopeCalculator.add(scope, offset))
                }
            }
        }
            .onEach { logI("added to list: $it") }
            .filter { scopeCalculator.isCurrentOrFutureScope(it) }

        val firstItem = items.firstOrNull()
        logI("First item in list: $firstItem (${firstItem?.let { scopeCalculator.isCurrentScope(it) }})")
        val prevKey = when {
            firstItem == null -> null
            scopeCalculator.isCurrentScope(firstItem) -> null
            else -> scopeCalculator.add(firstItem, -1)
        }

        val nextKey = when (val lastItem = items.lastOrNull()) {
            null -> null
            else -> scopeCalculator.add(lastItem, 1)
        }

        return LoadResult.Page(
            data = items,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}