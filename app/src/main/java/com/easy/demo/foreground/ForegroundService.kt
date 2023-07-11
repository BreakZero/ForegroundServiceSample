package com.easy.demo.foreground

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ForegroundService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var counter = 0

    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var notificationManager: NotificationManager
    private var job: Job?= null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            UserAction.START.toString() -> start()
            UserAction.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(content: String) = NotificationCompat.Builder(this, "counter_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Keep Running")
        .setContentText(content)
        .build()

    private fun start() {
        if (job != null) return
        job = scope.launch {
            while (isActive) {
                ensureActive()
                delay(1000)
                notificationManager.notify(1, createNotification("Current Number: ${++counter}"))
            }
        }
        startForeground(1, createNotification("Current Number: $counter"))
    }

    override fun onDestroy() {
        job?.cancel()
        println("TAG: job is cancel: ${job?.isActive}")
        super.onDestroy()
    }

    enum class UserAction {
        START, STOP
    }
}