package com.example.waf3.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waf3.core.executor.TestExecutor
import com.example.waf3.core.model.*
import com.example.waf3.core.quota.QuotaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val okHttpClient: OkHttpClient,
    private val quotaManager: QuotaManager,
    private val historyRepository: com.example.waf3.core.repo.HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppState>(AppState.TestSelection)
    val uiState: StateFlow<AppState> = _uiState.asStateFlow()

    private val _quotaState = MutableStateFlow(QuotaInfo(used = 0, limit = 10, resetInSeconds = 0))
    val quotaState: StateFlow<QuotaInfo> = _quotaState.asStateFlow()

    private val _connectionStatus = MutableStateFlow(ConnectionStatus(target = "", path = "", connected = false))
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _selected = MutableStateFlow<List<Test>>(emptyList())
    val selected: StateFlow<List<Test>> = _selected.asStateFlow()

    init {
        viewModelScope.launch {
            quotaManager.getLimitFlow().collect { limit ->
                _quotaState.update { it.copy(limit = limit) }
            }
        }
        viewModelScope.launch {
            quotaManager.usedCountFlow().collect { used ->
                _quotaState.update { it.copy(used = used) }
            }
        }
        viewModelScope.launch {
            quotaManager.resetInSecondsFlow().collect { reset ->
                _quotaState.update { it.copy(resetInSeconds = reset) }
            }
        }
    }

    fun selectTest(test: Test) {
        _selected.update { it + test }
    }

    fun deselectTest(test: Test) {
        _selected.update { it.filterNot { t -> t.definition.id == test.definition.id } }
    }

    fun confirmTests() {
        _uiState.value = AppState.Configuration(_selected.value)
    }

    fun configureTest(test: Test, config: TestConfiguration) {
        _selected.update { list ->
            list.map { if (it.definition.id == test.definition.id) it.copy(config = config) else it }
        }
    }

    fun clearSelection() { _selected.value = emptyList() }

    fun pingConnection(target: String, path: String) {
        viewModelScope.launch {
            try {
                val start = System.nanoTime()
                val req = okhttp3.Request.Builder()
                    .url(if (target.endsWith('/') || path.startsWith('/')) "$target$path" else "$target/$path")
                    .method("HEAD", null)
                    .build()
                okHttpClient.newCall(req).execute().use {
                    val ms = java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
                    _connectionStatus.value = ConnectionStatus(target, path, latencyMs = ms, connected = it.isSuccessful, timestampMs = System.currentTimeMillis())
                }
            } catch (_: Throwable) {
                _connectionStatus.value = ConnectionStatus(target, path, latencyMs = null, connected = false, timestampMs = System.currentTimeMillis())
            }
        }
    }

    fun executeTests(target: String, path: String) {
        viewModelScope.launch {
            if (!quotaManager.canExecute()) return@launch
            quotaManager.recordExecution()
            _uiState.value = AppState.Testing(progress = 0f)

            val client = okHttpClient
            val results = mutableListOf<TestResult>()
            val items = _selected.value
            val total = items.size.coerceAtLeast(1)
            val sessionId = historyRepository.newSession(target, path)
            for ((index, t) in items.withIndex()) {
                val timeout = t.config.timeoutSec.coerceAtMost(TestExecutor.MAX_TIMEOUT_SEC)
                val exec = TestExecutor(
                    okHttpClient = client,
                    target = target,
                    path = path,
                    config = t.config.copy(timeoutSec = timeout)
                )
                val result = exec.execute(t)
                results.add(result)
                historyRepository.saveResult(sessionId, result)
                _uiState.value = AppState.Testing(progress = (index + 1f) / total)
            }
            _uiState.value = AppState.Results(results)
        }
    }
}


