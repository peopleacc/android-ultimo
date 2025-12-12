package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ultimo.vehicleapp.ViewModels.NotificationViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.model.AppNotification
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val notifications by notificationViewModel.notifications.collectAsState()
    val currentUser by sessionViewModel.user.collectAsState()

    // Subscribe to notifications when user is available
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            notificationViewModel.subscribeToOrderUpdates(id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onNavigate(Screen.Home.route) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Semua Notifikasi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                if (notifications.isNotEmpty()) {
                    TextButton(onClick = { notificationViewModel.markAllAsRead() }) {
                        Text(
                            text = "Tandai Dibaca",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Content
        if (notifications.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum ada notifikasi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Notifikasi akan muncul saat status pesanan Anda berubah",
                        fontSize = 14.sp,
                        color = TextTertiary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationListItem(
                        notification = notification,
                        onClick = {
                            notificationViewModel.markAsRead(notification.id)
                            onNavigate(Screen.OrderDetail.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationListItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val backgroundColor = when (notification.statusType) {
        "pending" -> Color(0xFFFFF8E1)
        "proses" -> Color(0xFFE3F2FD)
        "menunggu_pembayaran" -> Color(0xFFFFF3E0)
        "selesai" -> Color(0xFFE8F5E9)
        else -> BackgroundGray
    }

    val iconTint = when (notification.statusType) {
        "pending" -> Color(0xFFF57C00)
        "proses" -> PrimaryBlue
        "menunggu_pembayaran" -> Color(0xFFE65100)
        "selesai" -> Color(0xFF4CAF50)
        else -> TextSecondary
    }

    val icon = when (notification.statusType) {
        "pending" -> Icons.Default.Schedule
        "proses" -> Icons.Default.Build
        "menunggu_pembayaran" -> Icons.Default.Payment
        "selesai" -> Icons.Default.CheckCircle
        else -> Icons.Default.Notifications
    }

    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val timeText = dateFormat.format(Date(notification.timestampMillis))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (!notification.isRead) backgroundColor else Color.White,
        shadowElevation = if (!notification.isRead) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (!notification.isRead) iconTint.copy(alpha = 0.2f) else Gray200,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (!notification.isRead) iconTint else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 15.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(iconTint, RoundedCornerShape(5.dp))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextTertiary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeText,
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Order #${notification.orderId}",
                        fontSize = 12.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
