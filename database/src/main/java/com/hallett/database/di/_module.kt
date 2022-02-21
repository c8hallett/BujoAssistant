package com.hallett.database.di

import androidx.room.Room
import com.hallett.database.BujoAssDatabase
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val databaseModule = DI.Module("database_module") {
    bindSingleton<BujoAssDatabase> {
        Room.databaseBuilder(
            instance(),
            BujoAssDatabase::class.java,
            "bujo_ass_database"
        ).build()
    }

    bindSingleton<com.hallett.database.TaskDao> {
        instance<BujoAssDatabase>().bujoTaskDao()
    }
}
