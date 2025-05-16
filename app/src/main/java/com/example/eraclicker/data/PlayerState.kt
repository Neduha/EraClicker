package com.example.eraclicker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_state")
data class PlayerState(
    @PrimaryKey val id: Int = 0,
    val resources: Int,
    val clickPower: Int,
    val passiveIncome: Int,
    val currentEra: Int
)
