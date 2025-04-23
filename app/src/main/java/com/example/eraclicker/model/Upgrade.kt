package com.example.eraclicker.model

data class Upgrade(
    val id: Int,
    val name: String,
    val baseCost: Int,
    val bonus: Int,
    val type: UpgradeType,
    val era: Int,
    val imageRes: Int,
    val level: Int = 0
) {
    val cost: Int
        get() = baseCost * (level + 1)
}
