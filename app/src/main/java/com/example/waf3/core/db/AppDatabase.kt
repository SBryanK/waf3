package com.example.waf3.core.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TestSessionEntity::class, TestResultEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): TestSessionDao
    abstract fun resultDao(): TestResultDao
}


