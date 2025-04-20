package com.example.eraclicker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eraclicker.viewmodel.GameViewModel

@Composable
fun UpgradeScreen(
    navController: NavHostController,
    gameVm:         GameViewModel
) {
    Column(
        modifier           = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LazyColumn(
            modifier           = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(gameVm.upgrades) { upgrade ->
                Row(
                    modifier           = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${upgrade.name} Lv.${upgrade.level} • Cost: ${upgrade.cost}")
                    Button(
                        onClick = { gameVm.buyUpgrade(upgrade.id) },
                        enabled = gameVm.resources >= upgrade.cost
                    ) {
                        Text("Buy")
                    }
                }
            }
        }
    }
}
