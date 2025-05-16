package com.example.eraclicker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UpgradeStateDao {
    @Query("SELECT * FROM upgrade_state")
    fun getAll(): List<UpgradeState>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(state: UpgradeState): Long
}
