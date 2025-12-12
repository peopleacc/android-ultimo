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
    object OrderDetail : Screen("order_detail")
    object Catalog : Screen("catalog")
    object CatalogDesign : Screen("catalog_design")
    object Notifications : Screen("notifications")
    object ForgotPassword : Screen("forgot_password")
    object VerifyOTP : Screen("verify_otp")
    object ResetPassword : Screen("reset_password")
    object ChangePassword : Screen("change_password")
}


