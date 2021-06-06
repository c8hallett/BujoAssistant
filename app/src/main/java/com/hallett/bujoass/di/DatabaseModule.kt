package com.hallett.bujoass.di

import com.hallett.bujoass.database.BujoAssDatabase
import com.hallett.bujoass.database.BujoTaskDao
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val databaseModule = Kodein.Module("database_module"){
    bind<BujoAssDatabase>() with singleton {
        BujoAssDatabase.create(instance())
    }

    bind<BujoTaskDao>() with singleton {
        instance<BujoAssDatabase>().bujoTaskDao()
    }
}