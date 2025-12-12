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
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip

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
    onLogout: (() -> Unit)? = null,
    pemesananViewModel: PemesananViewModel = viewModel()
) {
    val user by sessionViewModel.user.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val token by sessionViewModel.token.collectAsState()
    
    // Order counts from PemesananViewModel
    val totalSelesai by pemesananViewModel.totalSelesai.collectAsState()
    val totalProses by pemesananViewModel.totalProses.collectAsState()
    val totalPending by pemesananViewModel.totalPending.collectAsState()
    val pemesananList by pemesananViewModel.pemesanan.collectAsState()
    val totalOrders = pemesananList.size
    
    var hasTriedFetch by remember { mutableStateOf(false) }

    // Refresh user data jika user null tapi token ada (hanya sekali)
    LaunchedEffect(token) {
        val currentToken = token
        if (user == null && !currentToken.isNullOrEmpty() && !isLoading && !hasTriedFetch) {
            hasTriedFetch = true
            sessionViewModel.fetchUserDataAfterLogin(currentToken)
        }
    }
    
    // Load order counts when user is available
    LaunchedEffect(user?.id) {
        user?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 100)
            pemesananViewModel.loadTotalSelesai(id)
            pemesananViewModel.loadTotalProses(id)
            pemesananViewModel.loadTotalPending(id)
        }
    }
    
    // Reset hasTriedFetch jika user sudah ada
    LaunchedEffect(user) {
        if (user != null) {
            hasTriedFetch = false
        }
    }
    
    // Redirect ke Login jika tidak ada user setelah loading selesai
    LaunchedEffect(user, isLoading) {
        // Tunggu sampai loading selesai
        if (!isLoading) {
            // Jika user null (baik token ada atau tidak), redirect ke login
            if (user == null) {
                onNavigate(Screen.Login.route)
            }
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

    // Jika user null, tidak render apapun (akan redirect ke login)
    if (user == null) {
        // Tampilkan loading sementara redirect
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    // Jika user masih null setelah fetch (mungkin token invalid atau network error)
    // Tapi token masih ada, jadi biarkan user tetap bisa melihat UI dengan data default
    // Jangan langsung return error

    // 🔥 User data - gunakan data dari user jika ada, jika tidak gunakan placeholder
    val userProfile = mapOf(
        "name" to (user?.nama ?: "User"),
        "email" to (user?.email ?: "-"),
        "phone" to (user?.phone ?: "-"),
        "address" to (user?.address ?: "-"),
        "memberSince" to "2025", // Jika ingin dinamis, kasih dari API
        "totalOrders" to totalOrders,
        "completedOrders" to totalSelesai
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
                                if (user?.foto_profile != null) {
                                    AsyncImage(
                                        model = user?.foto_profile,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
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
                            "notifications" -> onNavigate(Screen.Notifications.route)
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
