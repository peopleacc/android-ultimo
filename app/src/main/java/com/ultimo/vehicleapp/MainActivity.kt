package com.ultimo.vehicleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.NavGraph
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomBottomNavigation
import com.ultimo.vehicleapp.ui.theme.VehicleSeatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehicleSeatAppTheme {
                // --- ViewModel dan Controller utama ---
                val navController = rememberNavController()
                val sessionViewModel: SessionViewModel = viewModel()

                val isLoading by sessionViewModel.isLoading.collectAsState()
                val token by sessionViewModel.token.collectAsState()

                // Gunakan derivedStateOf untuk menghindari recomposition yang tidak perlu
                // Hanya update startDestination setelah loading selesai
                val startDestination = derivedStateOf {
                    // Tunggu sampai loading selesai sebelum menentukan destination
                    if (isLoading) {
                        Screen.Login.route // Temporary value saat loading
                    } else {
                        if (token.isNullOrEmpty()) Screen.Login.route else Screen.Home.route
                    }
                }.value

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        // --- Tampilan loading (ambil session dulu) ---
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        else -> {
                            // --- Render NavGraph ---
                            Box(modifier = Modifier.fillMaxSize()) {
                                NavGraph(
                                    navController = navController,
                                    startDestination = startDestination,
                                    sessionViewModel = sessionViewModel
                                )

                                // --- Cek route aktif ---
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute =
                                    navBackStackEntry?.destination?.route ?: Screen.Login.route

                                // --- Bottom Navigation hanya muncul di screen tertentu ---
                                if (currentRoute in listOf(
                                        Screen.Home.route,
                                        Screen.Orders.route,
                                        Screen.Tracking.route,
                                        Screen.Profile.route
                                    )
                                ) {
                                    CustomBottomNavigation(
                                        currentRoute = currentRoute,
                                        onItemClick = { route ->
                                            navController.navigate(route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.BottomCenter)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
