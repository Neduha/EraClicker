package com.example.eraclicker.model

data class Upgrade(
    val id: Int,
    val name: String,
    val baseCost: Long,
    val bonus: Long,
    val type: UpgradeType,
    val era: Int,
    val imageRes: Int,
    val level: Int = 0
) {
    val cost: Long
        get() = baseCost * (level + 1)
}
