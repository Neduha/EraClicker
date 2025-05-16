package com.example.eraclicker.data

import com.example.eraclicker.data.PlayerState
import com.example.eraclicker.data.UpgradeState
import com.example.eraclicker.data.PlayerStateDao
import com.example.eraclicker.data.UpgradeStateDao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerState::class, UpgradeState::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerStateDao(): PlayerStateDao
    abstract fun upgradeStateDao(): UpgradeStateDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game_db"
                ).build().also { INSTANCE = it }
            }
    }
}
