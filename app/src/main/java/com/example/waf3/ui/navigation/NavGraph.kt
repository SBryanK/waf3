package com.example.waf3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.waf3.core.MainViewModel
import com.example.waf3.ui.screens.SecurityTestsScreen
import com.example.waf3.ui.screens.ConfirmScreen
import com.example.waf3.ui.screens.ResultsScreen
import com.example.waf3.ui.screens.HistoryScreen

object Routes {
    const val Tests = "tests"
    const val Confirm = "confirm"
    const val Results = "results"
    const val History = "history"
}

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = Routes.Tests) {
        composable(Routes.Tests) { SecurityTestsScreen(navController = navController, viewModel = viewModel) }
        composable(Routes.Confirm) { ConfirmScreen(navController = navController, viewModel = viewModel) }
        composable(Routes.Results) { ResultsScreen(navController = navController, viewModel = viewModel) }
        composable(Routes.History) { HistoryScreen(navController = navController, viewModel = viewModel) }
    }
}


