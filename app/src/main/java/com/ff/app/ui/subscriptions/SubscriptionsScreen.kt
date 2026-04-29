package com.ff.app.ui.subscriptions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(viewModel: SubscriptionsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servers") },
                actions = {
                    IconButton(onClick = { viewModel.runSpeedTest() }) { Icon(Icons.Default.Speed, "Test") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddDialog = true }) { Icon(Icons.Default.Add, "Add") }
        }
    ) { padding ->
        if (state.servers.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No servers yet", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(state.servers) { server ->
                    Card(modifier = Modifier.padding(8.dp).clickable { viewModel.connectTo(server) }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Dns, null)
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(server.name, style = MaterialTheme.typography.titleMedium)
                                Text(server.configUrl, style = MaterialTheme.typography.bodySmall)
                            }
                            if (server.ping > 0) Text("\${server.ping}ms", style = MaterialTheme.typography.labelSmall)
                            IconButton(onClick = { viewModel.connectTo(server) }) { Icon(Icons.Default.PlayArrow, "Connect") }
                        }
                    }
                }
            }
        }
    }
    if (viewModel.showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddDialog = false },
            title = { Text("Add Server") },
            text = {
                Column {
                    OutlinedTextField(viewModel.newName, { viewModel.newName = it }, label = { Text("Name") })
                    OutlinedTextField(viewModel.newConfig, { viewModel.newConfig = it }, label = { Text("URL / vmess://...") })
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.confirmAdd() }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { viewModel.showAddDialog = false }) { Text("Cancel") } }
        )
    }
}
