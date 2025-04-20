package com.example.eraclicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.eraclicker.model.Upgrade
import com.example.eraclicker.model.UpgradeType

class GameViewModel : ViewModel() {

    var resources by mutableStateOf(0)
        private set

    var clickPower by mutableStateOf(1)
        private set

    var passiveIncome by mutableStateOf(0)
        private set

    var upgrades = mutableStateListOf(
        Upgrade(1, "Auto Clicker",  10,  1, UpgradeType.PASSIVE),
        Upgrade(2, "Click Booster", 50,  5, UpgradeType.CLICK)
    )

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (passiveIncome > 0) {
                    resources += passiveIncome
                }
            }
        }
    }

    fun onClickResource() {
        resources += clickPower
    }

    fun buyUpgrade(id: Int) {
        val index = upgrades.indexOfFirst { it.id == id }
        if (index >= 0) {
            val up = upgrades[index]
            if (resources >= up.cost) {
                resources -= up.cost
                upgrades[index] = up.copy(level = up.level + 1)
                when (up.type) {
                    UpgradeType.CLICK   -> clickPower    += up.bonus
                    UpgradeType.PASSIVE -> passiveIncome += up.bonus
                }
            }
        }
    }
}
