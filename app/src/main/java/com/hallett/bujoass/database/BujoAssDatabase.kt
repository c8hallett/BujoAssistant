package com.hallett.bujoass.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [BujoTaskEntity::class],
    version = 1
)
@TypeConverters(
    DateToLongConverter::class,
    ScopeToStringTypeConverter::class
)
abstract class BujoAssDatabase: RoomDatabase() {
    abstract fun bujoTaskDao(): BujoTaskDao

    companion object{
        fun create(appContext: Context): BujoAssDatabase =
            Room.databaseBuilder(
                appContext,
                BujoAssDatabase::class.java,
                "bujo_ass_database"
            ).build()
    }
}