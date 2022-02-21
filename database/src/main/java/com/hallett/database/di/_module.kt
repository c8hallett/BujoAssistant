package com.hallett.database.di

import androidx.room.Room
import com.hallett.database.ITaskRepository
import com.hallett.database.TaskRepository
import com.hallett.database.room.BujoAssDatabase
import com.hallett.database.room.TaskDao
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

    bindSingleton<TaskDao> {
        instance<BujoAssDatabase>().bujoTaskDao()
    }

    bindSingleton<ITaskRepository> {
        TaskRepository(instance(), instance(), instance())
    }
}
