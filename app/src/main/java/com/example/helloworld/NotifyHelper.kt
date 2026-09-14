package com.example.helloworld

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Crea y muestra la notificación persistente con los botones "Reiniciar" y
 * "Detener". No usa un servicio en primer plano (Android 14+ exige declarar
 * un foregroundServiceType y sin eso la app crashea al iniciarlo) — con una
 * notificación normal alcanza para controlar Firefox Focus.
 */
object NotifyHelper {

    const val CHANNEL_ID = "focus_notify_channel"
    const val NOTIF_ID = 1001
    const val ACTION_RESTART = "com.example.helloworld.ACTION_RESTART"
    const val ACTION_STOP = "com.example.helloworld.ACTION_STOP"

    fun showNotification(context: Context) {
        createChannel(context)

        val restartIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_RESTART
        }
        val restartPending = PendingIntent.getBroadcast(
            context, 0, restartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_STOP
        }
        val stopPending = PendingIntent.getBroadcast(
            context, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Focus Notify")
            .setContentText("Controlá Firefox Focus")
            .addAction(android.R.drawable.ic_menu_revert, "Reiniciar", restartPending)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Detener", stopPending)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIF_ID, notification)
    }

    fun cancelNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(NOTIF_ID)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Focus Notify", NotificationManager.IMPORTANCE_LOW
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
