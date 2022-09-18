package com.hallett.taskassistant.features.scopeSelection

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.hallett.domain.coroutines.DispatchersWrapper
import com.hallett.scopes.model.Scope
import com.hallett.scopes.model.ScopeType
import com.hallett.taskassistant.main.di.PagerParams
import kotlinx.coroutines.flow.flowOn

class ScopeSelectionInfoGenerator(
    private val generatePager: (PagerParams) -> Pager<Scope, Scope>,
    private val dispatchers: DispatchersWrapper
) {

    private val pagingConfig = PagingConfig(pageSize = 20)
    fun generateInfo(scopeType: ScopeType): ScopeSelectionInfo {
        val scopes = generatePager(PagerParams(pagingConfig, scopeType))
            .flow
            .flowOn(dispatchers.default)

        return ScopeSelectionInfo(scopeType, scopes)
    }
}