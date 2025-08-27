package com.example.waf3.ui.screens

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
import com.example.waf3.core.model.TestConfiguration

@Composable
fun ConfirmScreen(navController: NavController, viewModel: MainViewModel) {
    val selected = viewModel.selected.collectAsState()
    val target = remember { mutableStateOf("https://example.com") }
    val path = remember { mutableStateOf("/") }
    val timeout = remember { mutableStateOf(10f) }
    val followRedirects = remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Confirm & Configure", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(target.value, { target.value = it }, label = { Text("Target") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(path.value, { path.value = it }, label = { Text("Path") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text("Timeout: ${timeout.value.toInt()}s")
        Slider(value = timeout.value, onValueChange = { timeout.value = it }, valueRange = 1f..30f)
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Switch(followRedirects.value, { followRedirects.value = it })
            Text("Follow redirects")
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(selected.value.size) { idx ->
                val t = selected.value[idx]
                ElevatedCard(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(t.definition.name, style = MaterialTheme.typography.titleSmall)
                        // Minimal customization placeholder
                    }
                }
            }
        }

        Button(
            onClick = {
                // apply global config
                selected.value.forEach { test ->
                    viewModel.configureTest(test, test.config.copy(timeoutSec = timeout.value.toInt(), followRedirects = followRedirects.value))
                }
                viewModel.executeTests(target.value, path.value)
                navController.navigate(com.example.waf3.ui.navigation.Routes.Results)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Run Tests") }
    }
}


