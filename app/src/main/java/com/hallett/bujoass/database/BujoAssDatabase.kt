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
}