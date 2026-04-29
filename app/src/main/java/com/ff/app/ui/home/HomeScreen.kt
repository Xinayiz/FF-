package com.ff.app.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ff.app.ui.theme.WaveBackground
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val uiState by homeViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.connected) {
            WaveBackground(speed = uiState.speed / 1000f, amplitude = 60f)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.connected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val angle by animateFloatAsState(
                        targetValue = if (uiState.connected) 360f else 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing)
                        )
                    )
                    Icon(
                        imageVector = if (uiState.connected) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = if (uiState.connected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(48.dp)
                            .rotate(if (uiState.connected) angle else 0f)
                    )
                    Text(
                        text = if (uiState.connected) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.connected) {
                        Text("Server: \${uiState.currentServer}", style = MaterialTheme.typography.bodyMedium)
                        Text("Protocol: \${uiState.coreName}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { homeViewModel.toggleConnection() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.connected) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (uiState.connected) "Disconnect" else "Connect")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Core & Bypass", style = MaterialTheme.typography.titleMedium)
            Row {
                listOf("FF Native", "Xray", "Sing-Box").forEach { core ->
                    FilterChip(
                        selected = uiState.coreName == core,
                        onClick = { homeViewModel.selectCore(core) },
                        label = { Text(core) },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                listOf("Speed", "Balanced", "Paranoid").forEach { mode ->
                    FilterChip(
                        selected = uiState.bypassMode == mode,
                        onClick = { homeViewModel.selectBypassMode(mode) },
                        label = { Text(mode) },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.connected) {
                Text("Real-Time Speed", style = MaterialTheme.typography.titleMedium)
                AndroidView(
                    factory = { ctx ->
                        LineChart(ctx).apply {
                            val entries = (0..10).map { i -> Entry(i.toFloat(), (Math.random() * 100).toFloat()) }
                            val dataSet = LineDataSet(entries, "KB/s").apply {
                                color = android.graphics.Color.BLUE
                            }
                            data = LineData(dataSet)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Quick Toggles", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                QuickToggle(Icons.Default.SplitScreen, "Split", uiState.splitTunnel) { homeViewModel.toggleSplitTunnel() }
                QuickToggle(Icons.Default.Block, "Ads", uiState.blockAds) { homeViewModel.toggleBlockAds() }
                QuickToggle(Icons.Default.Shield, "Kill", uiState.killswitch) { homeViewModel.toggleKillswitch() }
                QuickToggle(Icons.Default.VisibilityOff, "AI", uiState.aiBypass) { homeViewModel.toggleAiBypass() }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Data Usage", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                StatItem("Session", uiState.sessionRx, uiState.sessionTx)
                StatItem("Month", uiState.monthRx, uiState.monthTx)
            }
        }
    }
}

@Composable
fun QuickToggle(icon: ImageVector, label: String, checked: Boolean, onToggle: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label)
        Switch(checked = checked, onCheckedChange = { onToggle() })
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun StatItem(title: String, rx: Long, tx: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.labelSmall)
        Text("\${rx / 1024} kB ↓", style = MaterialTheme.typography.bodySmall)
        Text("\${tx / 1024} kB ↑", style = MaterialTheme.typography.bodySmall)
    }
}
