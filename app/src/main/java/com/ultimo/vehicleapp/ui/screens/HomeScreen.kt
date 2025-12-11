package com.ultimo.vehicleapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberMarkerState
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.ViewModels.NotificationViewModel
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.ViewModels.ProductViewModel
import com.ultimo.vehicleapp.ViewModels.ProgressViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.model.AppNotification
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SeatDesign(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val badge: String,
    val badgeColor: Color
)

data class ActiveOrder(
    val id: String,
    val service: String,
    val status: String,
    val progress: Int,
    val estimatedTime: String
)

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    productViewModel: ProductViewModel = viewModel(),
    pemesananViewModel: PemesananViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel(),
    notificationViewModel: NotificationViewModel = viewModel()
    ) {
    val products by productViewModel.products.collectAsState()
    val currentUser by sessionViewModel.user.collectAsState()
    val userOrders by pemesananViewModel.pemesanan.collectAsState()
    val progressList by progressViewModel.progress.collectAsState()
    val totalSelesai by pemesananViewModel.totalSelesai.collectAsState()
    
    // Notification states
    val notifications by notificationViewModel.notifications.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val showPopup by notificationViewModel.showPopup.collectAsState()
    val latestNotification by notificationViewModel.latestNotification.collectAsState()
    
    // Auto-dismiss popup after 4 seconds
    LaunchedEffect(showPopup) {
        if (showPopup) {
            kotlinx.coroutines.delay(4000)
            notificationViewModel.dismissPopup()
        }
    }
    
    // StatCard 1: Total Pending (pending + waiting for order) - dari PemesananController
    val totalPending by pemesananViewModel.totalPending.collectAsState()
    
    // StatCard 2: Total Proses - dari PemesananController (t_pemesanan dengan status proses)
    val totalProses by pemesananViewModel.totalProses.collectAsState()

    // Load pemesanan for user
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 20)
            progressViewModel.loadProgressForUser(id, limit = 20)
            pemesananViewModel.loadTotalSelesai(id)
            
            // Load data untuk StatCard
            pemesananViewModel.loadTotalPending(id)  // pending + waiting for order dari PemesananController
            pemesananViewModel.loadTotalProses(id)  // proses dari PemesananController (t_pemesanan)
            
            // Subscribe to realtime notifications
            notificationViewModel.subscribeToOrderUpdates(id)
        }
    }

    // Function to check status and navigate
    fun handleOrderNavigation() {
        val latestPendingOrder = userOrders.firstOrNull { order ->
            order.status_pengerjaan?.lowercase() == "pending" || 
            order.status_pengerjaan?.lowercase() == "proses" ||
            order.status_pengerjaan?.lowercase() == "Menunggu Pembayaran"
        }

        if (latestPendingOrder != null) {
            onNavigate(Screen.OrderDetail.route)
        } else {
            onNavigate(Screen.Catalog.route)
        }
    }
    
    // Cek apakah ada order dengan status "waiting for payment"
    val waitingPaymentOrder = userOrders.firstOrNull { order ->
        order.status_pengerjaan?.lowercase() == "Menunggu Pembayaran" ||
        order.status_pengerjaan?.lowercase() == "Menunggu Pembayaran"
    }

    val seatDesigns = products.map { p ->
        SeatDesign(
            id = p.product_id,
            name = p.nama_layanan,
            price = p.harga?.toInt() ?: 0,
            imageUrl = p.gambar_url ?: "",
            badge = "New",
            badgeColor = PrimaryBlue
        )
    }

    // Filter orders dengan status pending atau waiting - dari PemesananController
    val pendingWaitingOrders = userOrders.filter { order ->
        val status = order.status_pengerjaan?.lowercase()
        status == "pending" || status == "waiting" || status == "waiting for order" || status == "waiting for payment" || status == "waiting to payment"
    }

    // Filter progress dengan status proses - dari ProgressController
    val prosesProgressOrders = progressList.filter { progress ->
        val status = progress.t_pemesanan?.status_pengerjaan?.lowercase()
        status == "proses"
    }

    // Filter orders dengan status Menunggu Pembayaran - dari PemesananController
    val menungguPembayaranOrders = userOrders.filter { order ->
        val status = order.status_pengerjaan?.lowercase()
        status == "menunggu pembayaran"
    }

    // Cek apakah ada active order (dari salah satu atau kedua sumber)
    val hasActiveOrders = pendingWaitingOrders.isNotEmpty() || prosesProgressOrders.isNotEmpty() || menungguPembayaranOrders.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome Back! 👋",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Let's upgrade your car seats",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { notificationViewModel.markAllAsRead() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        // Badge for unread notifications
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(18.dp)
                                    .background(Color.Red, RoundedCornerShape(9.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Quick Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // StatCard 1: Pending (dari PemesananController - status: pending, waiting for order)
                    StatCard(totalPending.toString(), "Pending", modifier = Modifier.weight(1f))
                    // StatCard 2: Proses (dari PemesananController - t_pemesanan dengan status: proses)
                    StatCard(totalProses.toString(), "Proses", modifier = Modifier.weight(1f))
                    // StatCard 3: Selesai (dari PemesananController - status: selesai)
                    StatCard(totalSelesai.toString(), "Selesai", modifier = Modifier.weight(1f))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Notification Section (if there are notifications)
            if (notifications.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notifikasi Terbaru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = { onNavigate(Screen.Notifications.route) }) {
                        Text(
                            text = "Lihat Semua",
                            fontSize = 14.sp,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryBlue
                        )
                    }
                }
                
                notifications.take(3).forEach { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = {
                            notificationViewModel.markAsRead(notification.id)
                            onNavigate(Screen.OrderDetail.route)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            val context = LocalContext.current
            val storeName = "Ultimo Vehicle Seat"
            val storeAddress = "jl pisangan lama 2 no 3"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Location Shop",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            CustomCardWithBorder(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Gray200)
                    ) {
                        val storeLatLng = LatLng(-6.15476, 106.84891)
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(storeLatLng, 16f)
                        }
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            Marker(state = rememberMarkerState(position = storeLatLng), title = storeName)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$storeName, $storeAddress",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    CustomButton(
                        text = "Open in maps",
                        onClick = {
                            val lat = -6.211200302693269
                            val lng = 106.87662092496542
                            val label = Uri.encode(storeName)
                            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Map
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Catalog
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Katalog Jok Design",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                TextButton(onClick = { onNavigate(Screen.Catalog.route) }) {
                    Text(
                        text = "View All",
                        fontSize = 14.sp,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PrimaryBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(seatDesigns.take(2)) { design ->
                    SeatDesignCard(
                        design = design,
                        onClick = { 
                            // Navigate to CatalogDesignScreen with selected product
                            onNavigate("${Screen.CatalogDesign.route}/${design.id}")
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Lakukan Pembayaran (jika ada order dengan status waiting for payment)


            // CTA Button
            CustomButton(
                text = "Order New Service",
                onClick = { handleOrderNavigation() },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.ShoppingCart
            )
        }
    }
    
        // Popup Notification Overlay
        androidx.compose.animation.AnimatedVisibility(
            visible = showPopup && latestNotification != null,
            enter = androidx.compose.animation.slideInVertically(
                initialOffsetY = { -it }
            ) + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.slideOutVertically(
                targetOffsetY = { -it }
            ) + androidx.compose.animation.fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            latestNotification?.let { notification ->
                NotificationPopupCard(
                    notification = notification,
                    onDismiss = { notificationViewModel.dismissPopup() },
                    onClick = {
                        notificationViewModel.dismissPopup()
                        notificationViewModel.markAsRead(notification.id)
                        onNavigate(Screen.OrderDetail.route)
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    showStar: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showStar) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun SeatDesignCard(
    design: SeatDesign,
    onClick: () -> Unit
) {
    CustomCardWithBorder(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .height(320.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gray200),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (design.imageUrl.isEmpty()) R.drawable.black else design.imageUrl,
                    contentDescription = design.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.black),
                    placeholder = painterResource(R.drawable.black)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = design.badgeColor
                    ) {
                        Text(
                            text = design.badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = design.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start From",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = design.price.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomButton(
                text = "Choose Design",
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                variant = com.ultimo.vehicleapp.ui.components.ButtonVariant.Primary
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val backgroundColor = when (notification.statusType) {
        "pending" -> Color(0xFFFFF8E1) // Light amber
        "proses" -> Color(0xFFE3F2FD) // Light blue
        "menunggu_pembayaran" -> Color(0xFFFFF3E0) // Light orange
        "selesai" -> Color(0xFFE8F5E9) // Light green
        else -> BackgroundGray
    }
    
    val iconTint = when (notification.statusType) {
        "pending" -> Color(0xFFF57C00) // Amber
        "proses" -> PrimaryBlue
        "menunggu_pembayaran" -> Color(0xFFE65100) // Dark orange
        "selesai" -> Color(0xFF4CAF50) // Green
        else -> TextSecondary
    }
    
    val icon = when (notification.statusType) {
        "pending" -> Icons.Default.Schedule
        "proses" -> Icons.Default.Build
        "menunggu_pembayaran" -> Icons.Default.Payment
        "selesai" -> Icons.Default.CheckCircle
        else -> Icons.Default.Notifications
    }

    val timeText = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(notification.timestamp))

    CustomCardWithBorder(
        onClick = onClick,
        borderColor = if (!notification.isRead) iconTint else Gray200,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = timeText,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(iconTint, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun NotificationPopupCard(
    notification: AppNotification,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor = when (notification.statusType) {
        "pending" -> Color(0xFFFFF8E1)
        "proses" -> Color(0xFFE3F2FD)
        "menunggu_pembayaran" -> Color(0xFFFFF3E0)
        "selesai" -> Color(0xFFE8F5E9)
        else -> Color.White
    }
    
    val iconTint = when (notification.statusType) {
        "pending" -> Color(0xFFF57C00)
        "proses" -> PrimaryBlue
        "menunggu_pembayaran" -> Color(0xFFE65100)
        "selesai" -> Color(0xFF4CAF50)
        else -> PrimaryBlue
    }
    
    val icon = when (notification.statusType) {
        "pending" -> Icons.Default.Schedule
        "proses" -> Icons.Default.Build
        "menunggu_pembayaran" -> Icons.Default.Payment
        "selesai" -> Icons.Default.CheckCircle
        else -> Icons.Default.Notifications
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        shadowElevation = 8.dp,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconTint.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 2
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
