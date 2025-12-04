package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*

data class MenuItem(
    val id: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val action: String,
    val hasToggle: Boolean = false,
    val toggleValue: Boolean = false
)

@Composable
fun ProfileScreen(
    sessionViewModel: SessionViewModel,
    onNavigate: (String) -> Unit,
    onLogout: (() -> Unit)? = null
) {
    val user by sessionViewModel.user.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val token by sessionViewModel.token.collectAsState()

    // Refresh user data jika user null tapi token ada (misalnya setelah login)
    LaunchedEffect(Unit) {
        val currentToken = token
        if (user == null && !currentToken.isNullOrEmpty()) {
            sessionViewModel.fetchUserDataAfterLogin(currentToken)
        }
    }

    // Jika masih loading dari server
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Jika user null → token invalid → logout
    if (user == null) {
        Text("User tidak ditemukan / token invalid")
        return
    }

    // 🔥 User sudah ada → gunakan langsung
    val userProfile = mapOf(
        "name" to (user?.nama ?: "-"),
        "email" to (user?.email ?: "-"),
        "phone" to (user?.phone ?: "-"),
        "address" to (user?.address ?: "-"),
        "memberSince" to "2025", // Jika ingin dinamis, kasih dari API
        "totalOrders" to 0,
        "completedOrders" to 0
    )

    var notificationsEnabled by remember { mutableStateOf(true) }

    val menuItems = listOf(
        MenuItem(1, Icons.Default.Person, "Personal Information", "Update your personal details", "edit-profile"),
        MenuItem(2, Icons.Default.Notifications, "Notifications", "Manage notification preferences", "notifications", true, notificationsEnabled),
        MenuItem(3, Icons.Default.Help, "Help & Support", "Get help with your orders", "support")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(PrimaryBlue, PrimaryBlueLight)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigate(Screen.Home.route) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Card
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userProfile["name"]?.toString()
                                        ?.split(" ")
                                        ?.mapNotNull { it.firstOrNull()?.toString() }
                                        ?.joinToString("") ?: "JD",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile["name"] as String,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Member since ${userProfile["memberSince"]}",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                        IconButton(onClick = { /* Edit Profile */ }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = PrimaryBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = userProfile["totalOrders"].toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "Total Orders",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = userProfile["completedOrders"].toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "Completed",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 🔹 Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Contact Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            CustomCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                ContactInfoRow(Icons.Default.Email, "Email", userProfile["email"] as String)
                Spacer(modifier = Modifier.height(16.dp))
                ContactInfoRow(Icons.Default.Phone, "Phone", userProfile["phone"] as String)
                Spacer(modifier = Modifier.height(16.dp))
                ContactInfoRow(Icons.Default.LocationOn, "Address", userProfile["address"] as String)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            menuItems.forEach { item ->
                CustomCardWithBorder(
                    onClick = {
                        when (item.action) {
                            "edit-profile" -> onNavigate(Screen.PersonalInfo.route)
                            "support" -> onNavigate(Screen.HelpSupport.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BackgroundGray
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        if (item.hasToggle) {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Logout
            CustomCardWithBorder(
                onClick = { onLogout?.invoke() },
                borderColor = ErrorRed.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ErrorRed.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Logout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ErrorRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Version 1.0.0",
                fontSize = 12.sp,
                color = TextTertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BackgroundGray
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier
                    .size(40.dp)
                    .padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
