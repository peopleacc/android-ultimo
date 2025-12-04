package com.ultimo.vehicleapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.ui.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    sessionViewModel:  SessionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLogin = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onRegister = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Order.route) {
            ServiceOrderScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->5
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Tracking.route) {
            OrderTrackingScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Orders.route) {
            OrderHistoryScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Payment.route) {
            PaymentScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PersonalInfo.route) {
            PersonalInformationScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
    }
}


