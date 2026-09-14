package com.example.helloworld

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Recibe los toques de los botones de la notificación persistente. */
class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotifyForegroundService.ACTION_RESTART -> restartFocus(context)
            NotifyForegroundService.ACTION_STOP ->
                context.stopService(Intent(context, NotifyForegroundService::class.java))
        }
    }

    /** Mismo flujo probado en focusbot: borrar sesión, ir a Inicio, reabrir la URL. */
    private fun restartFocus(context: Context) {
        val controller = BrowserController(context)
        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.Main).launch {
            val url = appContext.getSharedPreferences("focus_notify_config", Context.MODE_PRIVATE)
                .getString("url", "wikipedia.org") ?: "wikipedia.org"
            controller.erase()
            delay(500)
            controller.goHome()
            delay(500)
            controller.openUrl(url)
        }
    }
}
