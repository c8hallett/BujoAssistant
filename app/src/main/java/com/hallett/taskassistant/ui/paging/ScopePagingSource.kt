package com.hallett.taskassistant.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hallett.scopes.IScopeGenerator
import com.hallett.scopes.Scope

class ScopePagingSource(private val scopeGenerator: IScopeGenerator, val includePastScopes: Boolean = false): PagingSource<Scope, Scope>() {
    override fun getRefreshKey(state: PagingState<Scope, Scope>): Scope? = when(val anchor = state.anchorPosition){
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
                for (offset in (number + 1) .. 0) {
                    add(scopeGenerator.add(scope, offset))
                }
            }
            else -> mutableListOf<Scope>().apply {
                for (offset in 0 until number) { // 0 .. (number - 1)
                    add(scopeGenerator.add(scope, offset))
                }
            }
        }.filter { scopeGenerator.isCurrentOrFutureScope(it) }

        val firstItem = items.firstOrNull()
        val prevKey = when {
            firstItem == null -> null
            scopeGenerator.isCurrentScope(firstItem) -> null
            else -> scopeGenerator.add(firstItem, -1)
        }

        val nextKey = when(val lastItem = items.lastOrNull()){
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