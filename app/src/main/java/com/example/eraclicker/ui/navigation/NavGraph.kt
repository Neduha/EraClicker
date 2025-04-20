package com.example.eraclicker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eraclicker.ui.screens.MainScreen
import com.example.eraclicker.ui.screens.UpgradeScreen
import com.example.eraclicker.ui.screens.StatsScreen
import com.example.eraclicker.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object Main     : Screen("main")
    object Upgrades : Screen("upgrades")
    object Stats    : Screen("stats")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val gameVm: GameViewModel = viewModel()
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                navController = navController,
                gameVm        = gameVm
            )
        }
        composable(Screen.Upgrades.route) {
            UpgradeScreen(
                navController = navController,
                gameVm        = gameVm
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
    }
}
