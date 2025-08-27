package com.example.waf3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.waf3.core.MainViewModel
import com.example.waf3.core.model.AppState
import com.example.waf3.core.model.Verdict

@Composable
fun ResultsScreen(navController: NavController, viewModel: MainViewModel) {
    val state = viewModel.uiState.collectAsState()
    when (val s = state.value) {
        is AppState.Results -> ResultsList(s)
        is AppState.Testing -> Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator(progress = { s.progress }) }
        else -> Text("No results")
    }
}

@Composable
private fun ResultsList(s: AppState.Results) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(s.results.size) { idx ->
            val r = s.results[idx]
            val expanded = remember { mutableStateOf(false) }
            ElevatedCard(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("${r.testName} [${r.verdict}]")
                    Text("Status: ${r.statusCode ?: "-"} ${r.statusMessage ?: ""}")
                    Text("Latency: ${r.latencyMs ?: "-"}ms")
                    Text("Final URL: ${r.finalUrl ?: "-"}")
                    if (!r.requestId.isNullOrBlank()) Text("Request ID: ${r.requestId}")
                    TextButton(onClick = { expanded.value = !expanded.value }) { Text(if (expanded.value) "Hide" else "Expand") }
                    AnimatedVisibility(expanded.value) {
                        Column {
                            Text("Headers:")
                            r.headers.forEach { (k, v) -> Text("$k: $v", style = MaterialTheme.typography.bodySmall) }
                            Spacer(Modifier.height(6.dp))
                            Text("Body:")
                            Text(r.bodyPreview ?: "", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}


