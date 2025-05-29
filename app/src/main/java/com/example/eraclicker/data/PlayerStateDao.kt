package com.example.eraclicker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlayerStateDao {
    @Query("SELECT * FROM player_state WHERE id = 0")
    suspend fun get(): PlayerState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: PlayerState)
}
