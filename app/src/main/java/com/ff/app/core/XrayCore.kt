package com.ff.app.core

import android.content.Context
import android.content.Intent
import libv2ray.Libv2ray

class XrayCore(private val context: Context) : VpnCore() {
    private var started = false

    class XrayProxyService : android.app.Service() {
        override fun onBind(intent: Intent?) = null
        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val config = intent?.getStringExtra("config") ?: ""
            Libv2ray.startV2ray(config)
            started = true
            return START_STICKY
        }
        override fun onDestroy() {
            Libv2ray.stopV2ray()
            started = false
            super.onDestroy()
        }
    }

    override fun start(config: String) {
        val intent = Intent(context, XrayProxyService::class.java).apply {
            putExtra("config", config)
        }
        context.startService(intent)
        started = true
    }

    override fun stop() {
        context.stopService(Intent(context, XrayProxyService::class.java))
        started = false
    }
    override fun pause() = stop()
    override fun resume() { /* перезапуск */ }
    override fun isRunning() = started
}
