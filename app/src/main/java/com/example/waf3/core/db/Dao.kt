package com.example.waf3.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TestSessionDao {
    @Insert
    suspend fun insert(session: TestSessionEntity): Long

    @Query("SELECT * FROM test_session ORDER BY createdAtMs DESC")
    fun observeAll(): Flow<List<TestSessionEntity>>

    @Query("DELETE FROM test_session WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface TestResultDao {
    @Insert
    suspend fun insert(result: TestResultEntity): Long

    @Query("SELECT * FROM test_result WHERE sessionId = :sessionId ORDER BY id ASC")
    fun observeBySession(sessionId: Long): Flow<List<TestResultEntity>>

    @Query("DELETE FROM test_result WHERE sessionId = :sessionId")
    suspend fun deleteBySession(sessionId: Long)
}


