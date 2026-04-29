package com.ff.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ff.app.FFApplication
import com.ff.app.core.AiBypassEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val connected: Boolean = false,
    val coreName: String = "FF Native",
    val bypassMode: String = "Balanced",
    val currentServer: String = "",
    val speed: Float = 0f,
    val splitTunnel: Boolean = false,
    val blockAds: Boolean = true,
    val killswitch: Boolean = true,
    val aiBypass: Boolean = true,
    val sessionRx: Long = 0L,
    val sessionTx: Long = 0L,
    val monthRx: Long = 0L,
    val monthTx: Long = 0L
)

class HomeViewModel : ViewModel() {
    private val app = FFApplication()
    private val coreManager = app.coreManager
    private val aiEngine = AiBypassEngine()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    internal var vpnPermissionRequest: (() -> Unit)? = null
    private var pendingStart = false

    fun toggleConnection() {
        val state = _uiState.value
        if (state.connected) {
            coreManager.stop()
            _uiState.value = state.copy(connected = false, speed = 0f)
        } else {
            if (coreManager.isVpnPrepared()) {
                actuallyStartVpn()
            } else {
                pendingStart = true
                vpnPermissionRequest?.invoke()
            }
        }
    }

    fun actuallyStartVpn() {
        viewModelScope.launch {
            val state = _uiState.value
            aiEngine.setMode(state.bypassMode)
            val serverConfig = com.ff.app.config.SubscriptionManager.getCurrentConfig()
            coreManager.start(state.coreName, serverConfig)
            _uiState.value = state.copy(connected = true, currentServer = "Connected")
            startStats()
        }
    }

    private suspend fun startStats() {
        while (_uiState.value.connected) {
            delay(1000)
            val (rx, tx) = coreManager.getTraffic()
            _uiState.value = _uiState.value.copy(
                sessionRx = rx,
                sessionTx = tx,
                speed = rx.toFloat() / 1024f
            )
        }
    }

    fun selectCore(core: String) { _uiState.value = _uiState.value.copy(coreName = core) }
    fun selectBypassMode(mode: String) {
        _uiState.value = _uiState.value.copy(bypassMode = mode)
        aiEngine.setMode(mode)
    }
    fun toggleSplitTunnel() { _uiState.value = _uiState.value.copy(splitTunnel = !_uiState.value.splitTunnel) }
    fun toggleBlockAds() { _uiState.value = _uiState.value.copy(blockAds = !_uiState.value.blockAds) }
    fun toggleKillswitch() { _uiState.value = _uiState.value.copy(killswitch = !_uiState.value.killswitch) }
    fun toggleAiBypass() { _uiState.value = _uiState.value.copy(aiBypass = !_uiState.value.aiBypass) }
}
