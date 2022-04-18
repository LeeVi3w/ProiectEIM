package com.example.proiecteim

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.lang.Runnable

class AlertService : Service() {
    private val channelId = "Notification from Service"
    private lateinit var runnable: Runnable
    private var isServiceStarted = false
    private var wakeLock: PowerManager.WakeLock? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val channel =
            NotificationChannel(
                channelId,
                "Alert channel",
                NotificationManager.IMPORTANCE_HIGH
            )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
}

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("SERVICE", Build.VERSION.SDK_INT.toString())
        val input = intent?.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Example Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.background)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        startService()

        return START_NOT_STICKY
    }

    private fun startService() {
        if (isServiceStarted) return
        Toast.makeText(this, "Service is starting", Toast.LENGTH_SHORT).show()
        isServiceStarted = true

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                acquire(10*60*1000L /*10 minutes*/)
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {

                    Log.d("ServiceRepeat", "SERVICE SERVICE SERVICE SERVICE")
                }
                delay(1 * 3 * 1000)
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}