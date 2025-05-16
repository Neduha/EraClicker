package com.example.eraclicker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eraclicker.model.Upgrade
import com.example.eraclicker.model.UpgradeType
import com.example.eraclicker.ui.navigation.Screen
import com.example.eraclicker.viewmodel.GameViewModel

@Composable
fun UpgradeScreen(
    navController: NavHostController,
    gameVm:        GameViewModel
) {
    val unlocked = gameVm.upgrades
        .filter { it.era <= gameVm.currentEra && it.type != UpgradeType.ERA }

    val eraAdvance = gameVm.upgrades
        .firstOrNull { it.era == gameVm.currentEra && it.type == UpgradeType.ERA }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text  = "Upgrades — ${gameVm.currentEraName}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(unlocked) { up ->
                UpgradeRow(up = up, gameVm = gameVm)
            }
            item {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(thickness = 2.dp)
                Spacer(Modifier.height(16.dp))
                eraAdvance?.let { up ->
                    UpgradeRow(up = up, gameVm = gameVm)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(onClick = { navController.navigate(Screen.Main.route) }) {
            Text("Back to Game")
        }
    }
}

@Composable
private fun UpgradeRow(
    up:     Upgrade,
    gameVm: GameViewModel
) {

    val displayCost = up.cost * (up.level + 1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment   = Alignment.CenterVertically
    ) {
        Column {
            Text(up.name, style = MaterialTheme.typography.bodyLarge)
            Text("Level: ${up.level}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = when (up.type) {
                    UpgradeType.CLICK   -> "+${up.bonus} per click"
                    UpgradeType.PASSIVE -> "+${up.bonus}/sec"
                    UpgradeType.ERA     -> "Advance to next era"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("Cost: $displayCost", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { gameVm.buyUpgrade(up.id) },
                enabled = gameVm.resources >= displayCost
            ) {
                Text("Buy")
            }
        }
    }
}
