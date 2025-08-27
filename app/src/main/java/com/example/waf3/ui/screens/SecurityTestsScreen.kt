package com.example.waf3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.waf3.core.MainViewModel
import com.example.waf3.core.model.TestCategory
import com.example.waf3.core.model.TestDefinition
import com.example.waf3.core.repo.TemplateRepository

@Composable
fun SecurityTestsScreen(modifier: Modifier = Modifier, navController: NavController, viewModel: MainViewModel) {
    val target = remember { mutableStateOf("https://example.com") }
    val path = remember { mutableStateOf("/") }
    val autoRefresh = remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val tests = remember { TemplateRepository(context).getTests() }
    val selectedState = viewModel.selected.collectAsState()
    val quotaState = viewModel.quotaState.collectAsState()
    val connState = viewModel.connectionStatus.collectAsState()

    LaunchedEffect(target.value, path.value, autoRefresh.value) {
        if (autoRefresh.value) {
            viewModel.pingConnection(target.value, path.value)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = target.value,
                    onValueChange = { target.value = it },
                    label = { Text("Target (https://example.com)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = path.value,
                    onValueChange = { path.value = it },
                    label = { Text("Path (/)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = autoRefresh.value, onCheckedChange = { autoRefresh.value = it })
                    Text("Auto-refresh")
                }
                Spacer(Modifier.height(8.dp))
                val limit = quotaState.value.limit.coerceAtLeast(1)
                LinearProgressIndicator(progress = { quotaState.value.used.toFloat() / limit }, modifier = Modifier.fillMaxWidth())
                Text("Quota: ${quotaState.value.used}/${quotaState.value.limit} · resets in ${quotaState.value.resetInSeconds / 60}m", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${target.value}${path.value} | Latency: ${connState.value.latencyMs ?: "-"}ms | ${if (connState.value.connected) "Connected" else "Failed"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            val grouped = tests.groupBy { it.category }
            grouped.forEach { (cat, items) ->
                item {
                    Text(
                        text = cat.name.replace('_', ' '),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            items.forEach { def ->
                                if (selectedState.value.none { it.definition.id == def.id })
                                    viewModel.selectTest(com.example.waf3.core.model.Test(def))
                            }
                        }) { Text("Select All") }
                    }
                }
                items(items.size) { idx ->
                    val def = items[idx]
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(def.name, style = MaterialTheme.typography.titleSmall)
                                AnimatedVisibility(visible = def.description.isNotBlank()) {
                                    Text(def.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Checkbox(
                                checked = selectedState.value.any { it.definition.id == def.id },
                                onCheckedChange = {
                                    if (it) viewModel.selectTest(com.example.waf3.core.model.Test(def)) else viewModel.deselectTest(com.example.waf3.core.model.Test(def))
                                }
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(88.dp)) }
        }

        AnimatedVisibility(visible = selectedState.value.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${selectedState.value.size} selected", textAlign = TextAlign.Start)
                FloatingActionButton(onClick = {
                    viewModel.confirmTests()
                    navController.navigate(com.example.waf3.ui.navigation.Routes.Confirm)
                }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                }
            }
        }
    }
}

// Loaded from template via TemplateRepository


