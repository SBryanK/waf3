package com.example.waf3.core.model

import kotlinx.serialization.Serializable

enum class TestCategory { WAF_DETECTION, BOT_DETECTION, HTTP_TLS, CDN, DDOS_LITE }

enum class Verdict { BLOCKED, WARNING, PASSED, ERROR }

enum class ExportFormat { JSON, CSV }

@Serializable
data class TestDefinition(
    val id: String,
    val name: String,
    val category: TestCategory,
    val description: String = "",
    val defaultMethod: String = "GET",
    val defaultPathSuffix: String = "",
    val defaultHeaders: Map<String, String> = emptyMap(),
    val defaultBody: String? = null,
)

@Serializable
data class TestConfiguration(
    val timeoutSec: Int = 10,
    val followRedirects: Boolean = true,
    val methodOverride: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val concurrency: Int? = null,
    val intervalMs: Long? = null,
    val sizeBytes: Long? = null,
)

data class Test(
    val definition: TestDefinition,
    val config: TestConfiguration = TestConfiguration()
)

data class TLSInfo(
    val protocol: String? = null,
    val cipherSuite: String? = null,
    val alpnProtocol: String? = null,
)

data class ConnectionStatus(
    val target: String,
    val path: String,
    val latencyMs: Long? = null,
    val timestampMs: Long = System.currentTimeMillis(),
    val connected: Boolean = false
)

data class QuotaInfo(
    val used: Int,
    val limit: Int,
    val resetInSeconds: Long
)

data class TestResult(
    val id: String,
    val testName: String,
    val category: TestCategory,
    val statusCode: Int?,
    val statusMessage: String?,
    val latencyMs: Long?,
    val finalUrl: String?,
    val requestId: String?,
    val headers: Map<String, String>,
    val bodyPreview: String?,
    val tlsInfo: TLSInfo?,
    val error: String? = null,
    val verdict: Verdict
)

sealed class AppState {
    object TestSelection : AppState()
    data class Configuration(val tests: List<Test>) : AppState()
    data class Testing(val progress: Float) : AppState()
    data class Results(val results: List<TestResult>) : AppState()
}

