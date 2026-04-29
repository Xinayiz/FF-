package com.ff.app.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AiSettingsState(
    val aggression: Float = 0.7f,
    val autoMode: Boolean = true,
    val manualProfile: String = "Balanced",
    val blocksAvoided: Int = 42,
    val successRate: Int = 98
)

class SettingsViewModel : ViewModel() {
    private val _aiState = MutableStateFlow(AiSettingsState())
    val aiState: StateFlow<AiSettingsState> = _aiState

    fun setAggression(v: Float) { _aiState.value = _aiState.value.copy(aggression = v) }
    fun toggleAutoMode() { _aiState.value = _aiState.value.copy(autoMode = !_aiState.value.autoMode) }
    fun setManualProfile(p: String) { _aiState.value = _aiState.value.copy(manualProfile = p) }
}
