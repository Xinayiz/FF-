package com.ff.app.core

import android.content.Context
import android.content.Intent
import io.nekohasekai.libbox.Libbox

class SingBoxCore(private val context: Context) : VpnCore() {
    private var started = false

    class SingBoxProxyService : android.app.Service() {
        override fun onBind(intent: Intent?) = null
        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val config = intent?.getStringExtra("config") ?: ""
            Libbox.start(config)
            started = true
            return START_STICKY
        }
        override fun onDestroy() {
            Libbox.stop()
            started = false
            super.onDestroy()
        }
    }

    override fun start(config: String) {
        val intent = Intent(context, SingBoxProxyService::class.java).apply {
            putExtra("config", config)
        }
        context.startService(intent)
        started = true
    }

    override fun stop() {
        context.stopService(Intent(context, SingBoxProxyService::class.java))
        started = false
    }
    override fun pause() = stop()
    override fun resume() { /* перезапуск */ }
    override fun isRunning() = started
}
