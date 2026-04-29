package com.ff.app.ui.subscriptions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ff.app.config.ConfigParser
import com.ff.app.config.SubscriptionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ServerInfo(val id: String, val name: String, val configUrl: String, var ping: Int = -1)

data class SubscriptionsUiState(val servers: List<ServerInfo> = listOf())

class SubscriptionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionsUiState())
    val uiState: StateFlow<SubscriptionsUiState> = _uiState

    var showAddDialog by mutableStateOf(false)
    var newName by mutableStateOf("")
    var newConfig by mutableStateOf("")

    fun confirmAdd() {
        if (newName.isNotEmpty() && newConfig.isNotEmpty()) {
            val parsed = ConfigParser.parse(newConfig)
            SubscriptionManager.addConfig(parsed)
            loadServers()
            showAddDialog = false
            newName = ""
            newConfig = ""
        }
    }

    fun loadServers() {
        val list = SubscriptionManager.getAllConfigs().mapIndexed { i, cfg ->
            ServerInfo("\$i", "Server \$i", cfg)
        }
        _uiState.value = SubscriptionsUiState(list)
    }

    fun runSpeedTest() {
        viewModelScope.launch {
            val updated = _uiState.value.servers.map { it.copy(ping = kotlin.random.Random.nextInt(10, 200)) }
            _uiState.value = SubscriptionsUiState(updated)
        }
    }

    fun connectTo(server: ServerInfo) {
        SubscriptionManager.setCurrentConfig(server.configUrl)
        // вызов соединения через HomeViewModel
    }
}
