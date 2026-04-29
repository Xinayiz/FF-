package com.ff.app.core

import java.security.SecureRandom
import java.util.concurrent.atomic.AtomicBoolean

class AiBypassEngine {
    private val active = AtomicBoolean(false)
    private val paused = AtomicBoolean(false)
    private var mode = "Balanced"

    fun setMode(mode: String) { this.mode = mode }

    fun start() { active.set(true); paused.set(false) }
    fun pause() { paused.set(true) }
    fun resume() { paused.set(false) }
    fun shutdown() { active.set(false) }
    fun isActive() = active.get() && !paused.get()

    fun applyBypass(data: ByteArray): ByteArray {
        if (!isActive()) return data
        return when (mode) {
            "Paranoid" -> fragmentPackets(data, 4) + randomizeTls(data)
            "Speed" -> data
            else -> fragmentPackets(data, 2)
        }
    }

    private fun fragmentPackets(data: ByteArray, maxCount: Int): ByteArray {
        if (data.size < 10) return data
        val rand = SecureRandom()
        val fragments = mutableListOf<ByteArray>()
        var offset = 0
        while (offset < data.size) {
            val size = rand.nextInt(data.size / (maxCount + 1), data.size / maxCount).coerceIn(1, data.size - offset)
            fragments.add(data.sliceArray(offset..offset + size - 1))
            offset += size
        }
        return fragments.reduce { acc, bytes -> acc + bytes }
    }

    private fun randomizeTls(data: ByteArray): ByteArray {
        if (data.size < 16) return data
        val copy = data.clone()
        copy[5] = (copy[5].toInt() xor 0x01).toByte()
        return copy
    }
}
