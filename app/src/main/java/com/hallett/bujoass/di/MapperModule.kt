package com.hallett.bujoass.di

import com.hallett.bujoass.database.task.BujoTaskEntity
import com.hallett.bujoass.domain.usecase.mapper.*
import com.hallett.bujoass.presentation.model.Task
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

val mapperModule = Kodein.Module("mapper_module") {
    bind<Mapper<BujoTaskEntity, Task>>() with singleton { BujoTaskEntityToTaskMapper() }
}