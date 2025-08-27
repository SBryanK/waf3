package com.example.waf3.core.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "test_session")
data class TestSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val target: String,
    val path: String,
    val createdAtMs: Long
)

@Entity(tableName = "test_result")
data class TestResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val testId: String,
    val testName: String,
    val category: String,
    val statusCode: Int?,
    val statusMessage: String?,
    val latencyMs: Long?,
    val finalUrl: String?,
    val requestId: String?,
    val headersJson: String,
    val bodyPreview: String?,
    val tlsProtocol: String?,
    val tlsCipher: String?,
    val alpn: String?,
    val verdict: String,
    val error: String?
)


