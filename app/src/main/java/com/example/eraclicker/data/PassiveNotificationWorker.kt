package com.example.eraclicker.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.eraclicker.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PassiveNotificationWorker(
    val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val db = AppDatabase.getInstance(appContext)
    private val playerDao = db.playerStateDao()


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {


        val ps = playerDao.getPlayerState()
        if (ps == null) {

            return@withContext Result.success()
        }


        val now = System.currentTimeMillis()
        val deltaSec = ((now - ps.lastUpdate) / 1000).toInt().coerceAtLeast(0)




        val earned = deltaSec.toLong() * ps.passiveIncome


        val updatedPlayerState = ps.copy(
            resources = ps.resources + earned,
            lastUpdate = now
        )
        playerDao.upsert(updatedPlayerState)

        showNotification(deltaSec, earned)

        return@withContext Result.success()
    }

    private fun showNotification(deltaSec: Int, earned: Long) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {

                return
            }

        }

        val hours = deltaSec / 3600
        val minutes = (deltaSec % 3600) / 60
        val channelId = "passive_income_channel"
        val notificationId = 1001
        val mgr = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "Idle Earnings"
            val channelDescription = "Notifications for passive income earned while away."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            mgr.createNotificationChannel(channel)

        }

        val title = "Passive Income!"
        val text = "You were away for ${hours}h ${minutes}m and earned $earned resources come and claim them."
        val smallIconResId = R.drawable.passive_notification_icon


        val notificationBuilder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(smallIconResId)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        mgr.notify(notificationId, notificationBuilder.build())


    }
}