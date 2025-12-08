package com.ultimo.vehicleapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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

        composable(
            route = "${Screen.Order.route}/{step}/{productId}",
            arguments = listOf(
                navArgument("step") {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument("productId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val step = backStackEntry.arguments?.getInt("step") ?: 1
            val productIdArg = backStackEntry.arguments?.getInt("productId") ?: 0
            val productId = if (productIdArg == 0) null else productIdArg
            ServiceOrderScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                initialStep = step,
                initialProductId = productId
            )
        }
        
        // Fallback route without arguments
        composable(Screen.Order.route) {
            ServiceOrderScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Tracking.route) {
            OrderTrackingScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                sessionViewModel = sessionViewModel
            )
        }

        composable(Screen.Orders.route) {
            OrderHistoryScreen(
                sessionViewModel = sessionViewModel,
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
                    // Jika navigate ke Login, clear back stack
                    if (route == Screen.Login.route) {
                        navController.navigate(route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(route)
                    }
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

        composable(Screen.OrderDetail.route) {
            OrderDetailScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(
            route = "${Screen.Catalog.route}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val productIdArg = backStackEntry.arguments?.getInt("productId") ?: 0
            val productId = if (productIdArg == 0) null else productIdArg
            CatalogScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                productId = productId
            )
        }
        
        // Fallback route without productId (for "View All")
        composable(Screen.Catalog.route) {
            CatalogScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
    }
}


