package com.ff.app.core

import android.content.Context
import android.content.Intent
import android.net.VpnService

class CoreManager(private val context: Context) {
    private val cores = mapOf(
        "FF Native" to FFNativeCore(),
        "Xray" to XrayCore(context),
        "Sing-Box" to SingBoxCore(context)
    )
    private var activeCore: VpnCore? = null

    fun start(coreName: String, config: String) {
        stop()
        activeCore = cores[coreName]
        activeCore?.start(config)
    }

    fun stop() {
        activeCore?.stop()
        activeCore = null
    }

    fun pause() {
        activeCore?.pause()
    }

    fun resume() {
        activeCore?.resume()
    }

    fun isRunning() = activeCore?.isRunning() == true

    fun getTraffic(): Pair<Long, Long> = activeCore?.getTraffic() ?: (0L to 0L)

    fun isVpnPrepared(): Boolean {
        val intent = VpnService.prepare(context)
        return intent == null
    }
}
