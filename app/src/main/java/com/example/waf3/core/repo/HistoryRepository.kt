package com.example.waf3.core.repo

import com.example.waf3.core.db.AppDatabase
import com.example.waf3.core.db.TestResultEntity
import com.example.waf3.core.db.TestSessionEntity
import com.example.waf3.core.model.TestResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HistoryRepository(private val db: AppDatabase) {
    private val json = Json { prettyPrint = false }

    suspend fun newSession(target: String, path: String): Long {
        return db.sessionDao().insert(TestSessionEntity(target = target, path = path, createdAtMs = System.currentTimeMillis()))
    }

    suspend fun saveResult(sessionId: Long, result: TestResult) {
        val entity = TestResultEntity(
            sessionId = sessionId,
            testId = result.id,
            testName = result.testName,
            category = result.category.name,
            statusCode = result.statusCode,
            statusMessage = result.statusMessage,
            latencyMs = result.latencyMs,
            finalUrl = result.finalUrl,
            requestId = result.requestId,
            headersJson = json.encodeToString(result.headers),
            bodyPreview = result.bodyPreview,
            tlsProtocol = result.tlsInfo?.protocol,
            tlsCipher = result.tlsInfo?.cipherSuite,
            alpn = result.tlsInfo?.alpnProtocol,
            verdict = result.verdict.name,
            error = result.error
        )
        db.resultDao().insert(entity)
    }
}


