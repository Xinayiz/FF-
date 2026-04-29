package com.ff.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.Settings

enum class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Default.Home),
    Subscriptions("subscriptions", "Servers", Icons.Default.RssFeed),
    Settings("settings", "Settings", Icons.Default.Settings)
}
