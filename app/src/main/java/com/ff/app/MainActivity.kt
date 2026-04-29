package com.ff.app

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ff.app.ui.theme.FFTheme
import com.ff.app.ui.home.HomeScreen
import com.ff.app.ui.home.HomeViewModel
import com.ff.app.ui.subscriptions.SubscriptionsScreen
import com.ff.app.ui.settings.SettingsScreen
import com.ff.app.ui.ai.AiBypassSettingsScreen
import com.ff.app.ui.navigation.BottomNavItem

class MainActivity : ComponentActivity() {
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            (viewModel as? HomeViewModel)?.apply { actuallyStartVpn() }
        }
    }

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FFTheme {
                homeViewModel = viewModel()
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    androidx.navigation.compose.NavHost(
                        navController = navController,
                        startDestination = BottomNavItem.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(BottomNavItem.Home.route) { HomeScreen(homeViewModel) }
                        composable(BottomNavItem.Subscriptions.route) { SubscriptionsScreen() }
                        composable(BottomNavItem.Settings.route) { SettingsScreen() }
                        composable("ai_bypass") { AiBypassSettingsScreen() }
                    }
                }
            }
        }
        homeViewModel.vpnPermissionRequest = {
            startActivityForResult(VpnService.prepare(this), 1)
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        BottomNavItem.values().forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) { launchSingleTop = true } },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
