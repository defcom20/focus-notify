package com.example.helloworld

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Servicio que observa y controla Firefox Focus.
 * Se expone como singleton para que BrowserController pueda usarlo.
 */
class FocusAccessibilityService : AccessibilityService() {

    companion object {
        const val TAG = "FocusNotify"

        @Volatile
        var instance: FocusAccessibilityService? = null
            private set

        const val ID_ERASE = "org.mozilla.focus:id/erase"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "AccessibilityService conectado")
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    private fun root(): AccessibilityNodeInfo? = rootInActiveWindow

    fun findById(viewId: String): AccessibilityNodeInfo? =
        root()?.findAccessibilityNodeInfosByViewId(viewId)?.firstOrNull()

    private fun walk(node: AccessibilityNodeInfo?): List<AccessibilityNodeInfo> {
        node ?: return emptyList()
        val out = mutableListOf(node)
        for (i in 0 until node.childCount) out += walk(node.getChild(i))
        return out
    }

    fun findByDesc(desc: String): AccessibilityNodeInfo? =
        walk(root()).firstOrNull { it.contentDescription?.toString()?.contains(desc, true) == true }

    /** click: si el nodo no es clickeable, sube por los padres buscando uno que sí lo sea */
    fun click(node: AccessibilityNodeInfo?): Boolean {
        var n = node
        var hops = 0
        while (n != null && hops < 6) {
            if (n.isClickable && n.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
            n = n.parent
            hops++
        }
        return false
    }

    fun clickById(viewId: String) = click(findById(viewId))

    /** Manda a Inicio para forzar que la app actual quede en segundo plano */
    fun goHome() = performGlobalAction(GLOBAL_ACTION_HOME)
}
