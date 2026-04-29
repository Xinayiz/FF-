package com.ff.app.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.ff.app.MainActivity
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class FFNativeCore : VpnCore() {
    private var vpnService: FFVpnService? = null
    private val engine = AiBypassEngine()
    private val running = AtomicBoolean(false)
    private var rxBytes = 0L
    private var txBytes = 0L

    class FFVpnService : VpnService() {
        companion object {
            var instance: FFVpnService? = null
        }
        lateinit var engine: AiBypassEngine
        private var vpnFd: ParcelFileDescriptor? = null
        private var serverSocket: ServerSocket? = null
        private val executor = Executors.newCachedThreadPool()

        override fun onCreate() {
            super.onCreate()
            instance = this
        }

        fun startVpn(engine: AiBypassEngine) {
            this.engine = engine
            // SOCKS5 на localhost:1080
            serverSocket = ServerSocket(1080, 50, InetSocketAddress("127.0.0.1", 1080).address)
            executor.submit { acceptSocks() }

            val builder = Builder()
                .setSession("FF Native")
                .addAddress("10.9.0.2", 24)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("1.1.1.1")
                .addDnsServer("8.8.8.8")
                .setConfigureIntent(PendingIntent.getActivity(
                    this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT))
            vpnFd = builder.establish()
            executor.submit { readTun() }
        }

        private fun readTun() {
            val fd = vpnFd ?: return
            val buffer = ByteArray(32767)
            while (engine.isActive()) {
                try {
                    val len = android.system.Os.read(fd.fileDescriptor, buffer, 0, buffer.size)
                    if (len > 0) {
                        val packet = buffer.copyOf(len)
                        val transformed = engine.applyBypass(packet)
                        // Отправляем обработанный пакет обратно в TUN (петля – для демонстрации, в реальности нужно в интернет)
                        android.system.Os.write(fd.fileDescriptor, transformed, 0, transformed.size)
                    }
                } catch (_: Exception) {}
            }
        }

        private fun acceptSocks() {
            while (engine.isActive()) {
                try {
                    val client = serverSocket?.accept() ?: break
                    executor.submit { handleSocksClient(client) }
                } catch (_: Exception) {}
            }
        }

        private fun handleSocksClient(client: Socket) {
            try {
                val input = client.getInputStream()
                val output = client.getOutputStream()
                val greeting = ByteArray(2)
                input.read(greeting)
                output.write(byteArrayOf(5, 0))
                val request = ByteArray(4)
                input.read(request)
                val addrType = request[1]
                val dstPort = ((request[2].toInt() and 0xFF) shl 8) or (request[3].toInt() and 0xFF)
                val dstAddr = when (addrType.toInt()) {
                    1 -> {
                        val ip = ByteArray(4)
                        input.read(ip)
                        ip.joinToString(".") { (it.toInt() and 0xFF).toString() }
                    }
                    3 -> {
                        val len = input.read()
                        val domain = ByteArray(len)
                        input.read(domain)
                        String(domain)
                    }
                    else -> null
                } ?: return

                val remote = Socket("127.0.0.1", 1081)  // прокси Xray
                output.write(byteArrayOf(5, 0, 0, 1, 0, 0, 0, 0, 0, 0))

                executor.submit { pipeWithBypass(input, remote.getOutputStream(), engine, upstream = true) }
                executor.submit { pipeWithBypass(remote.getInputStream(), output, engine, upstream = false) }
            } catch (_: Exception) {}
            finally { client.close() }
        }

        private fun pipeWithBypass(src: InputStream, dst: OutputStream, engine: AiBypassEngine, upstream: Boolean) {
            val buf = ByteArray(8192)
            try {
                var len: Int
                while (src.read(buf).also { len = it } != -1) {
                    val outBuf = if (upstream) engine.applyBypass(buf.copyOf(len)) else buf.copyOf(len)
                    dst.write(outBuf)
                    dst.flush()
                }
            } catch (_: Exception) {}
        }

        fun stopVpn() {
            engine.shutdown()
            serverSocket?.close()
            vpnFd?.close()
            vpnFd = null
        }
    }

    override fun start(config: String) {
        running.set(true)
        engine.start()
        val context = FFApplication.ctx
        val intent = Intent(context, FFVpnService::class.java)
        context.startService(intent)
        FFVpnService.instance?.let { service ->
            service.startVpn(engine)
            vpnService = service
        }
    }

    override fun stop() {
        running.set(false)
        vpnService?.stopVpn()
        vpnService = null
    }

    override fun pause() { engine.pause() }
    override fun resume() { engine.resume() }
    override fun isRunning() = running.get()
    override fun getTraffic() = rxBytes to txBytes
}
