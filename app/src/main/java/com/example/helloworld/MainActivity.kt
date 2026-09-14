package com.example.helloworld

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    private lateinit var controller: BrowserController
    private lateinit var status: TextView
    private lateinit var prefs: SharedPreferences
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = BrowserController(this)
        prefs = getSharedPreferences("focus_notify_config", MODE_PRIVATE)

        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }

        status = TextView(this)
        root.addView(status)

        input = EditText(this).apply {
            hint = "url"
            setText(prefs.getString("url", "wikipedia.org"))
        }
        root.addView(input, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))

        fun button(label: String, action: () -> Unit) {
            root.addView(Button(this).apply {
                text = label
                setOnClickListener { action() }
            }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }

        button("1. Activar accesibilidad") { controller.openAccessibilitySettings() }
        button("2. Activar notificación") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
                )
                toast("Concedé el permiso de notificaciones y volvé a tocar este botón")
            } else {
                NotifyHelper.showNotification(this)
                toast("Notificación activada")
            }
        }

        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        val pkg = controller.installedPackage() ?: "NO instalado"
        val svc = if (controller.isServiceEnabled()) "activo" else "DESACTIVADO"
        status.text = "Navegador: $pkg\nServicio de accesibilidad: $svc\n"
    }

    /** Guarda la URL configurada para que persista al salir de la app */
    override fun onPause() {
        super.onPause()
        prefs.edit().putString("url", input.text.toString()).apply()
    }

    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}
