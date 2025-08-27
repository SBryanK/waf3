package com.example.waf3.core.quota

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class QuotaManager(private val dataStore: DataStore<Preferences>) {
    private val windowSize: Duration = 30.minutes
    private val keyEvents = stringPreferencesKey("quota_events")
    private val keyLimit = longPreferencesKey("quota_limit")

    suspend fun setLimit(limit: Int) {
        dataStore.edit { it[keyLimit] = limit.toLong() }
    }

    fun getLimitFlow(default: Int = 10): Flow<Int> = dataStore.data.map { it[keyLimit]?.toInt() ?: default }

    suspend fun canExecute(nowMs: Long = System.currentTimeMillis()): Boolean {
        val (events, limit) = readState()
        val pruned = prune(events, nowMs)
        return pruned.size < limit
    }

    suspend fun recordExecution(nowMs: Long = System.currentTimeMillis()) {
        val (events, _) = readState()
        val pruned = prune(events, nowMs)
        val updated = (pruned + nowMs).joinToString(",")
        dataStore.edit { it[keyEvents] = updated }
    }

    fun usedCountFlow(nowMsProvider: () -> Long = { System.currentTimeMillis() }): Flow<Int> =
        dataStore.data.map { prefs ->
            val events = prefs[keyEvents].orEmpty()
            val list = events.split(',').filter { it.isNotBlank() }.mapNotNull { it.toLongOrNull() }
            prune(list, nowMsProvider()).size
        }

    fun resetInSecondsFlow(nowMsProvider: () -> Long = { System.currentTimeMillis() }): Flow<Long> =
        dataStore.data.map { prefs ->
            val events = prefs[keyEvents].orEmpty()
            val list = events.split(',').filter { it.isNotBlank() }.mapNotNull { it.toLongOrNull() }
            val now = nowMsProvider()
            val pruned = prune(list, now)
            if (pruned.isEmpty()) 0 else ((pruned.first() + windowSize.inWholeMilliseconds - now) / 1000).coerceAtLeast(0)
        }

    private suspend fun readState(): Pair<List<Long>, Int> {
        val prefs = dataStore.data.first()
        val events = prefs[keyEvents].orEmpty()
        val list = events.split(',').filter { it.isNotBlank() }.mapNotNull { it.toLongOrNull() }
        val limit = prefs[keyLimit]?.toInt() ?: 10
        return list to limit
    }

    private fun prune(events: List<Long>, nowMs: Long): List<Long> {
        val threshold = nowMs - windowSize.inWholeMilliseconds
        return events.filter { it >= threshold }.sorted()
    }
}


