package com.example.eraclicker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upgrade_state")
data class UpgradeState(
    @PrimaryKey val id: Int,
    val level: Int
)
