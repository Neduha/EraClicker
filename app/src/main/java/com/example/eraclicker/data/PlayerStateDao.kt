package com.example.eraclicker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlayerStateDao {


    @Query("SELECT * FROM player_state LIMIT 1")
    suspend fun getPlayerState(): PlayerState?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: PlayerState)

    @Query("SELECT id FROM player_state LIMIT 1")
    suspend fun getPlayerStateId(): Int?

}