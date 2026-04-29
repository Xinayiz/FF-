package com.ff.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController? = null) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            ExpandableGroup("Routing") {
                SettingItem("GeoIP", "US") {}
                SettingItem("Domain Rules", "123 rules") {}
            }
            ExpandableGroup("DNS") {
                SettingItem("Upstream", "1.1.1.1") {}
                SettingItem("DoH", "Enabled") {}
            }
            ExpandableGroup("AI Bypass") {
                SettingItem("Configure", "Adaptive obfuscation") {
                    navController?.navigate("ai_bypass")
                }
            }
            ExpandableGroup("Appearance") {
                SettingItem("Theme", "System") {}
            }
            ExpandableGroup("Backup") {
                SettingItem("Export", "Save settings") {}
            }
        }
    }
}

@Composable
fun ExpandableGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
        }
        if (expanded) {
            Divider()
            content()
        }
    }
}

@Composable
fun SettingItem(name: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name)
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
