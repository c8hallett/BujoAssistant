package com.hallett.bujoass.di

import androidx.room.Room
import com.hallett.bujoass.database.BujoAssDatabase
import com.hallett.bujoass.database.task.BujoTaskDao
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

val databaseModule = Kodein.Module("database_module"){
    bind<BujoAssDatabase>() with singleton {
        Room.databaseBuilder(
            instance(),
            BujoAssDatabase::class.java,
            "bujo_ass_database"
        ).build()
    }

    bind<BujoTaskDao>() with singleton {
        instance<BujoAssDatabase>().bujoTaskDao()
    }
}