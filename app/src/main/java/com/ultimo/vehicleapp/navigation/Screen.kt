package com.ultimo.vehicleapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Order : Screen("order")
    object Tracking : Screen("tracking")
    object Orders : Screen("orders")
    object Payment : Screen("payment")
    object Profile : Screen("profile")
    object PersonalInfo : Screen("personal_info")
    object HelpSupport : Screen("help_support")
}


