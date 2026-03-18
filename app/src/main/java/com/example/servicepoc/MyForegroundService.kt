package com.example.servicepoc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MyForegroundService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val CHANNEL_ID = "MyServiceChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Service Running")
            .setContentText("Logging data in the background...")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .build()

        // Start as foreground service
        startForeground(1, notification)

        // Start work
        startWork()

        return START_STICKY
    }

    private fun startWork() {
        serviceScope.launch {
            while (isActive) {
                Log.d("POCService", "Background task is active at ${System.currentTimeMillis()}")
                delay(5000)
            }
        }
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("POCService", "Service Destroyed")
    }
}
