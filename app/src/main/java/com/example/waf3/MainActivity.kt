package com.example.waf3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.waf3.ui.theme.Waf3Theme
import com.example.waf3.ui.navigation.AppNavGraph
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.waf3.core.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Waf3Theme {
                AppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val app = (androidx.compose.ui.platform.LocalContext.current.applicationContext as EdgeOneApp)
    val vm = androidx.lifecycle.viewmodel.compose.viewModel<MainViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val c = app.container
                return MainViewModel(
                    okHttpClient = c.okHttpClient,
                    quotaManager = c.quotaManager,
                    historyRepository = c.historyRepository
                ) as T
            }
        }
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        AppNavGraph(navController = navController, viewModel = vm)
    }
}

@Preview(showBackground = true)
@Composable
fun AppRootPreview() {
    Waf3Theme {
        AppRoot()
    }
}