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

        // Route with step, productId, and materialId (from CatalogDesignScreen)
        composable(
            route = "${Screen.Order.route}/{step}/{productId}/{materialId}",
            arguments = listOf(
                navArgument("step") {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument("productId") {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument("materialId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val step = backStackEntry.arguments?.getInt("step") ?: 1
            val productIdArg = backStackEntry.arguments?.getInt("productId") ?: 0
            val materialIdArg = backStackEntry.arguments?.getInt("materialId") ?: 0
            val productId = if (productIdArg == 0) null else productIdArg
            val materialId = if (materialIdArg == 0) null else materialIdArg
            ServiceOrderScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                initialStep = step,
                initialProductId = productId,
                initialMaterialId = materialId
            )
        }
        
        // Route with step and productId (legacy support)
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
                initialProductId = productId,
                initialMaterialId = null
            )
        }
        
        // Fallback route without arguments
        composable(Screen.Order.route) {
            ServiceOrderScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                },
                initialMaterialId = null
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

        // Payment route with pesananId
        composable(
            route = "${Screen.Payment.route}/{pesananId}",
            arguments = listOf(
                navArgument("pesananId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val pesananIdArg = backStackEntry.arguments?.getInt("pesananId") ?: 0
            val pesananId = if (pesananIdArg == 0) null else pesananIdArg
            PaymentScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                pesananId = pesananId
            )
        }
        
        // Fallback Payment route without pesananId
        composable(Screen.Payment.route) {
            PaymentScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                pesananId = null
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
                sessionViewModel = sessionViewModel,
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

        // CatalogDesign route with productId
        composable(
            route = "${Screen.CatalogDesign.route}/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            CatalogDesignScreen(
                sessionViewModel = sessionViewModel,
                productId = productId,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                sessionViewModel = sessionViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Forgot Password Flow
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                },
                onOTPSent = { email ->
                    navController.navigate("${Screen.VerifyOTP.route}/$email")
                }
            )
        }

        composable(
            route = "${Screen.VerifyOTP.route}/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OTPVerificationScreen(
                email = email,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route)
                    }
                },
                onVerified = { verifiedEmail, token ->
                    navController.navigate("${Screen.ResetPassword.route}/$verifiedEmail/$token")
                }
            )
        }

        composable(
            route = "${Screen.ResetPassword.route}/{email}/{token}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ResetPasswordScreen(
                email = email,
                token = token,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Login.route)
                    }
                },
                onSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}


