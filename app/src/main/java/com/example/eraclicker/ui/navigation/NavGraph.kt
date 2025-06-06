package com.example.eraclicker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Keep this
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eraclicker.ui.screens.MainScreen
import com.example.eraclicker.ui.screens.UpgradeScreen
import com.example.eraclicker.ui.screens.StatsScreen
import com.example.eraclicker.ui.screens.WelcomeBackScreen
import com.example.eraclicker.ui.screens.TimeTravelerLockoutScreen
import com.example.eraclicker.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object Main            : Screen("main")
    object Upgrades        : Screen("upgrades")
    object Stats           : Screen("stats")
    object WelcomeBack     : Screen("welcome_back")
    object TimeLockout     : Screen("time_lockout")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val gameVm: GameViewModel = viewModel()



    val startDestination = when {
        gameVm.showTimeLockoutScreen -> Screen.TimeLockout.route
        gameVm.showWelcomeBackScreen -> Screen.WelcomeBack.route
        else -> Screen.Main.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Main.route) {
            MainScreen(
                navController = navController,
                gameVm = gameVm
            )
        }
        composable(Screen.Upgrades.route) {
            UpgradeScreen(
                navController = navController,
                gameVm = gameVm
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(gameVm = gameVm)
        }
        composable(Screen.WelcomeBack.route) {
            WelcomeBackScreen(
                timeAway = gameVm.offlineTimeGainedString,
                resourcesEarned = gameVm.offlineResourcesGained,
                onCollectClicked = {
                    gameVm.clearWelcomeBackScreenFlag()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.WelcomeBack.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.TimeLockout.route) {
            TimeTravelerLockoutScreen(
                lockoutMessage = gameVm.lockoutMessage,
                remainingMillis = gameVm.remainingLockoutTimeForDisplayMillis
            )
        }
    }
}