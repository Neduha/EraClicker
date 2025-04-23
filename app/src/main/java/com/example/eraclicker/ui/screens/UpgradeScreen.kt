package com.example.eraclicker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eraclicker.model.UpgradeType
import com.example.eraclicker.viewmodel.GameViewModel

@Composable
fun UpgradeScreen(
    navController: NavHostController,
    gameVm:         GameViewModel
) {
    val eras = (1..gameVm.currentEra).toList()

    LazyColumn(
        modifier           = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        eras.forEach { era ->
            item {
                Text(
                    text  = "Era ${era}: ${gameVm.eraNames[era - 1]}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            val eraUpgrades = gameVm.upgrades.filter { it.era == era }
            val normalUpgrades = eraUpgrades.filter { it.type != UpgradeType.ERA }
            items(normalUpgrades) { upgrade ->
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${upgrade.name} Lv.${upgrade.level} • Cost: ${upgrade.cost} • " +
                                if (upgrade.type == UpgradeType.CLICK)
                                    "+${upgrade.bonus}/click"
                                else
                                    "+${upgrade.bonus}/s",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { gameVm.buyUpgrade(upgrade.id) },
                        enabled = gameVm.resources >= upgrade.cost
                    ) {
                        Text("Buy")
                    }
                }
            }
            item {
                Divider(
                    color     = Color.Gray,
                    thickness = 1.dp,
                    modifier  = Modifier.padding(vertical = 8.dp)
                )
            }
            item {
                val bridge = eraUpgrades.first { it.type == UpgradeType.ERA }
                Row(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text  = "${bridge.name} • Cost: ${bridge.cost}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { gameVm.buyUpgrade(bridge.id) },
                        enabled = bridge.level == 0 && gameVm.resources >= bridge.cost
                    ) {
                        Text(if (bridge.level == 0) "Unlock" else "Unlocked")
                    }
                }
            }
        }
    }
}
