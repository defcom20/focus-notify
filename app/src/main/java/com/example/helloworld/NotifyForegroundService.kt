package com.example.helloworld

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * Servicio en primer plano que muestra una notificación persistente con
 * botones "Reiniciar" y "Detener" para controlar Firefox Focus sin
 * necesidad de un botón flotante (evita el permiso de superposición y los
 * problemas de toque cuando Focus está en pantalla completa).
 */
class NotifyForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "focus_notify_channel"
        const val NOTIF_ID = 1001
        const val ACTION_RESTART = "com.example.helloworld.ACTION_RESTART"
        const val ACTION_STOP = "com.example.helloworld.ACTION_STOP"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Focus Notify", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val restartIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = ACTION_RESTART
        }
        val restartPending = PendingIntent.getBroadcast(
            this, 0, restartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = ACTION_STOP
        }
        val stopPending = PendingIntent.getBroadcast(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Focus Notify")
            .setContentText("Controlá Firefox Focus")
            .addAction(android.R.drawable.ic_menu_revert, "Reiniciar", restartPending)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Detener", stopPending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
