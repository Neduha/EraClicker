package com.example.eraclicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eraclicker.ui.navigation.Screen
import com.example.eraclicker.viewmodel.GameViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    gameVm:         GameViewModel
) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Gray)
                .clickable { gameVm.onClickResource() }
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text  = "Resources: ${gameVm.resources}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text  = "Passive: ${gameVm.passiveIncome}/s",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        Row {
            Button(onClick = { navController.navigate(Screen.Upgrades.route) }) {
                Text("Upgrades")
            }
            Spacer(Modifier.width(16.dp))
            Button(onClick = { navController.navigate(Screen.Stats.route) }) {
                Text("Stats")
            }
        }
    }
}
