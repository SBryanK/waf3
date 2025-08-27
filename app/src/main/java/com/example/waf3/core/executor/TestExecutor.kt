package com.example.waf3.core.executor

import com.example.waf3.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.URL
import java.util.concurrent.TimeUnit

class TestExecutor(
    private val okHttpClient: OkHttpClient,
    private val target: String,
    private val path: String,
    private val config: TestConfiguration
) {
    suspend fun execute(test: Test): TestResult = withContext(Dispatchers.IO) {
        val startNs = System.nanoTime()
        val client = okHttpClient.newBuilder()
            .followRedirects(config.followRedirects)
            .callTimeout(config.timeoutSec.toLong(), TimeUnit.SECONDS)
            .build()

        val method = test.config.methodOverride ?: test.definition.defaultMethod
        val url = buildUrl(target, path, test.definition.defaultPathSuffix)
        val headers = Headers.Builder().apply {
            test.definition.defaultHeaders.forEach { (k, v) -> add(k, v) }
            test.config.headers.forEach { (k, v) -> add(k, v) }
        }.build()
        val body: RequestBody? = when (method.uppercase()) {
            "POST", "PUT", "PATCH" -> {
                val payload = (test.config.body ?: test.definition.defaultBody) ?: ""
                val bytes = payload.toByteArray()
                require(bytes.size <= MAX_PAYLOAD_BYTES) { "Payload exceeds 10MB limit" }
                bytes.toRequestBody("application/x-www-form-urlencoded".toMediaType())
            }
            else -> null
        }

        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .method(method.uppercase(), body)
            .build()

        return@withContext try {
            client.newCall(request).execute().use { response ->
                val latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
                val tlsInfo = extractTlsInfo(response)
                val headerMap = response.headers.toMap()
                val bodyPreview = response.peekBody(MAX_PREVIEW_BYTES.toLong()).string()
                val finalUrl = response.request.url.toString()
                val requestId = headerMap["cf-ray"] ?: headerMap["x-request-id"]
                val verdict = applyVerdictHeuristics(response.code, bodyPreview, headerMap)

                TestResult(
                    id = test.definition.id,
                    testName = test.definition.name,
                    category = test.definition.category,
                    statusCode = response.code,
                    statusMessage = response.message,
                    latencyMs = latencyMs,
                    finalUrl = finalUrl,
                    requestId = requestId,
                    headers = headerMap,
                    bodyPreview = bodyPreview.safeTruncate(),
                    tlsInfo = tlsInfo,
                    verdict = verdict
                )
            }
        } catch (t: Throwable) {
            val latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
            TestResult(
                id = test.definition.id,
                testName = test.definition.name,
                category = test.definition.category,
                statusCode = null,
                statusMessage = null,
                latencyMs = latencyMs,
                finalUrl = null,
                requestId = null,
                headers = emptyMap(),
                bodyPreview = null,
                tlsInfo = null,
                error = t.message,
                verdict = Verdict.ERROR
            )
        }
    }

    private fun buildUrl(target: String, basePath: String, suffix: String): String {
        val base = if (target.endsWith('/') || basePath.startsWith('/')) "$target$basePath" else "$target/$basePath"
        return if (suffix.isBlank()) base else if (base.endsWith('/') || suffix.startsWith('/')) "$base$suffix" else "$base/$suffix"
    }

    private fun extractTlsInfo(response: Response): TLSInfo? {
        val handshake = response.handshake ?: return null
        return TLSInfo(
            protocol = handshake.tlsVersion.javaName,
            cipherSuite = handshake.cipherSuite.javaName,
            alpnProtocol = response.protocol.toString()
        )
    }

    private fun applyVerdictHeuristics(code: Int, body: String, headers: Map<String, String>): Verdict {
        val blockedCodes = setOf(403, 406, 409, 429, 451, 503, 566)
        if (code in blockedCodes) return Verdict.BLOCKED
        val text = body.lowercase()
        if (setOf("blocked", "denied", "waf").any { it in text }) return Verdict.BLOCKED

        val sqlErrorHints = listOf("sql", "syntax error", "mysql", "postgres", "odbc")
        val weakCsp = headers["content-security-policy"].isNullOrBlank()
        val hasSqlError = sqlErrorHints.any { it in text }
        val looksLikeFileContent = text.contains("root:x:") || text.contains("[internet shortcuts]")
        if (hasSqlError || looksLikeFileContent || weakCsp) return Verdict.WARNING

        return Verdict.PASSED
    }

    private fun String.safeTruncate(max: Int = MAX_PREVIEW_BYTES): String =
        if (length <= max) this else take(max) + "…"

    companion object {
        private const val MAX_PREVIEW_BYTES = 4096
        private const val MAX_PAYLOAD_BYTES = 10 * 1024 * 1024 // 10MB
        const val MAX_CONCURRENCY = 128
        const val MAX_TIMEOUT_SEC = 30
    }
}

private fun Headers.toMap(): Map<String, String> = names().associateWith { get(it) ?: "" }


