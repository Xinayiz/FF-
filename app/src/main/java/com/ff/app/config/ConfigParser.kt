package com.ff.app.config

import android.util.Base64

object ConfigParser {
    fun parse(raw: String): String {
        val trimmed = raw.trim()
        return when {
            trimmed.startsWith("vmess://") -> {
                val b64 = trimmed.removePrefix("vmess://")
                try {
                    String(Base64.decode(b64, Base64.DEFAULT))
                } catch (e: Exception) {
                    "{}"
                }
            }
            trimmed.startsWith("vless://") -> trimmed
            trimmed.startsWith("ss://") -> trimmed
            trimmed.startsWith("trojan://") -> trimmed
            trimmed.startsWith("hysteria2://") -> trimmed
            else -> {
                try {
                    val decoded = String(Base64.decode(trimmed, Base64.DEFAULT))
                    decoded.split("\n", "\r\n").firstOrNull() ?: decoded
                } catch (e: Exception) {
                    trimmed
                }
            }
        }
    }
}
