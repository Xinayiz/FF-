package com.ff.app.ui.ai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ff.app.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiBypassSettingsScreen(settingsViewModel: SettingsViewModel = viewModel()) {
    val state by settingsViewModel.aiState.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("AI Bypass Engine") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("AI dynamically adapts obfuscation.", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Aggression Level", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = state.aggression,
                onValueChange = { settingsViewModel.setAggression(it) },
                valueRange = 0f..1f,
                steps = 2
            )
            Row {
                Switch(checked = state.autoMode, onCheckedChange = { settingsViewModel.toggleAutoMode() })
                Text("Auto-select profile")
            }
            if (!state.autoMode) {
                listOf("Minimal", "Balanced", "Paranoid").forEach { profile ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = state.manualProfile == profile, onClick = { settingsViewModel.setManualProfile(profile) })
                        Text(profile)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Statistics", style = MaterialTheme.typography.titleMedium)
            Text("Blocks avoided today: \${state.blocksAvoided}")
            Text("Avg success rate: \${state.successRate}%")
        }
    }
}
