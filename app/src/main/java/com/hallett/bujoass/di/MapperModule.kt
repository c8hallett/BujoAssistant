package com.hallett.bujoass.di

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.model.DScope
import com.hallett.bujoass.domain.model.DScopeInstance
import com.hallett.bujoass.domain.usecase.mapper.*
import com.hallett.bujoass.presentation.model.PScope
import com.hallett.bujoass.presentation.model.PScopeInstance
import com.hallett.bujoass.presentation.model.Task
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val mapperModule = Kodein.Module("mapper_module") {
    bind<Mapper<DScope?, PScope>>() with singleton { DtoPScopeMapper() }
    bind<Mapper<PScope, DScope?>>() with singleton { PtoDScopeMapper() }
    bind<Mapper<DScopeInstance?, PScopeInstance>>() with singleton { DtoPScopeInstanceMapper(instance()) }
    bind<Mapper<PScopeInstance, DScopeInstance?>>() with singleton { PtoDScopeInstanceMapper(instance(), instance()) }

    bind<Mapper<BujoTaskEntity.ScopeInfo?, PScopeInstance>>() with singleton { ScopeInfoToPScopeInstanceMapper(instance()) }
    bind<Mapper<PScopeInstance, BujoTaskEntity.ScopeInfo?>>() with singleton { PScopeInstanceToScopeInfoMapper(instance(), instance()) }
    bind<Mapper<BujoTaskEntity, Task>>() with singleton { BujoTaskEntityToTaskMapper(instance(), instance()) }
}