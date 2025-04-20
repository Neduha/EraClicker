package com.example.eraclicker.model

enum class UpgradeType { CLICK, PASSIVE }

data class Upgrade(
    val id: Int,
    val name: String,
    val baseCost: Int,
    val bonus: Int,
    val type: UpgradeType,
    val level: Int = 0
) {
    val cost: Int
        get() = baseCost * (level + 1)
}
