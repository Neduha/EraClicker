package com.example.eraclicker

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.eraclicker.data.AppDatabase
import com.example.eraclicker.data.PassiveNotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.text.get

class EraClickerApp : Application(), DefaultLifecycleObserver {

    private val database by lazy { AppDatabase.getInstance(this) }
    private val playerDao by lazy { database.playerStateDao() }

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    override fun onStart(owner: LifecycleOwner) {
        WorkManager.getInstance(this).cancelUniqueWork("passive_notification")
    }

    override fun onStop(owner: LifecycleOwner) {
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            val currentPlayerState = playerDao.getPlayerState()
            val currentTimeForOnStop = System.currentTimeMillis()

            if (currentPlayerState == null) {
                Log.w("EraClickerApp", "onStop: currentPlayerState is NULL. Cannot update lastUpdate.")
            } else {

                playerDao.upsert(currentPlayerState.copy(lastUpdate = currentTimeForOnStop))

            }


            val req = OneTimeWorkRequestBuilder<PassiveNotificationWorker>()
                .setInitialDelay(24, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(this@EraClickerApp)
                .enqueueUniqueWork(
                    "passive_notification",
                    ExistingWorkPolicy.REPLACE,
                    req
                )

        }
    }
}