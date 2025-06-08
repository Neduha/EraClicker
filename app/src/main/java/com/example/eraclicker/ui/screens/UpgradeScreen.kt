package com.example.eraclicker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.eraclicker.ui.theme.getAccentColorForEra
import com.example.eraclicker.util.NumberFormatter
import com.example.eraclicker.viewmodel.GameViewModel

@Composable
fun UpgradeScreen(
    navController: NavHostController,
    gameVm: GameViewModel
) {
    val unlocked = gameVm.upgrades
        .filter { it.era <= gameVm.currentEra && it.type != UpgradeType.ERA }
        .sortedBy { it.cost * (it.level + 1) }

    val eraAdvance = gameVm.upgrades
        .firstOrNull { it.era == gameVm.currentEra && it.type == UpgradeType.ERA }

    val eraAccentColor = getAccentColorForEra(gameVm.currentEra)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Upgrades — ${gameVm.currentEraName}",
            style = MaterialTheme.typography.titleLarge,
            color = eraAccentColor
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(unlocked) { up ->
                UpgradeRow(up = up, gameVm = gameVm, accentColor = eraAccentColor)
                HorizontalDivider(thickness = 1.dp, color = eraAccentColor.copy(alpha = 0.3f))
            }
            if (unlocked.isNotEmpty() && eraAdvance != null) {
                item {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 2.dp, color = eraAccentColor)
                    Spacer(Modifier.height(16.dp))
                }
            }
            eraAdvance?.let { up ->
                item {
                    UpgradeRow(up = up, gameVm = gameVm, accentColor = eraAccentColor, isEraUpgrade = true)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { navController.navigate(Screen.Main.route) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = eraAccentColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Game")
        }
    }
}

@Composable
private fun UpgradeRow(
    up: Upgrade,
    gameVm: GameViewModel,
    accentColor: androidx.compose.ui.graphics.Color,
    isEraUpgrade: Boolean = false
) {
    val displayCost = if (up.type == UpgradeType.ERA) up.cost else up.cost * (up.level + 1)
    val currentBonus = if (up.type == UpgradeType.CLICK || up.type == UpgradeType.PASSIVE) {
        up.bonus * up.level
    } else {
        up.bonus
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(up.name, style = MaterialTheme.typography.titleMedium)
            if (up.type != UpgradeType.ERA) {
                Text("Level: ${up.level}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = when (up.type) {
                        UpgradeType.CLICK -> "Current: +${NumberFormatter.formatGeneric(currentBonus)} per click"
                        UpgradeType.PASSIVE -> "Current: +${NumberFormatter.formatGeneric(currentBonus)}/sec"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = when (up.type) {
                        UpgradeType.CLICK   -> "Next: +${NumberFormatter.formatGeneric(up.bonus)} per click"
                        UpgradeType.PASSIVE -> "Next: +${NumberFormatter.formatGeneric(up.bonus)}/sec"
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor
                )
            } else {
                Text(
                    text = "Advance to the next Era!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = accentColor
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text("Cost: ${NumberFormatter.formatGeneric(displayCost)}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = { gameVm.buyUpgrade(up.id) },
                enabled = gameVm.resources >= displayCost && (up.type != UpgradeType.ERA || up.level == 0),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Text(if (up.type == UpgradeType.ERA) "Advance" else "Buy")
            }
        }
    }
}