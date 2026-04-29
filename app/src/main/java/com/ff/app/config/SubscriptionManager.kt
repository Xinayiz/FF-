package com.ff.app.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object SubscriptionManager {
    private val configs = mutableListOf<String>()
    private var currentConfig: String = ""

    suspend fun loadFromUrl(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val content = URL(url).readText()
            val parsedConfigs = parseSubscriptionContent(content)
            configs.clear()
            configs.addAll(parsedConfigs)
            if (parsedConfigs.isNotEmpty()) {
                currentConfig = parsedConfigs.first()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun parseSubscriptionContent(content: String): List<String> {
        return content.lines()
            .filter { it.startsWith("vmess://") || it.startsWith("vless://") || it.startsWith("ss://") || it.startsWith("trojan://") || it.startsWith("hysteria2://") }
            .map { ConfigParser.parse(it) }
    }

    fun addConfig(config: String) {
        configs.add(config)
    }

    fun setCurrentConfig(config: String) {
        currentConfig = config
    }

    fun getCurrentConfig(): String = currentConfig

    fun getAllConfigs(): List<String> = configs.toList()
}
