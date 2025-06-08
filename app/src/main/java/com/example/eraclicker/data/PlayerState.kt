package com.example.eraclicker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerState(
    @PrimaryKey val id: Int = 0,
    val resources: Long = 1000L,
    val clickPower: Long = 1L,
    val passiveIncome: Long = 0L,
    val currentEra: Int = 1,
    val lastUpdate: Long = 0L,

    val totalOnlineTimeMillis: Long = 0L,
    val totalOfflineTimeMillis: Long = 0L,
    val totalResourcesEverEarned: Long = 0L,
    val totalManualClicks: Long = 0L,
    val totalResourcesFromClicks: Long = 0L
)
