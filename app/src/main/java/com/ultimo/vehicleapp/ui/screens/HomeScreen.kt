package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*
import com.ultimo.vehicleapp.ViewModels.ProductViewModel
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.ViewModels.ProgressViewModel

data class SeatDesign(
    val id: Int,
    val name: String,
    val price: Int,
    val imageResId: Int,
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
    progressViewModel: ProgressViewModel = viewModel()
    ) {
    val products by productViewModel.products.collectAsState()
    val currentUser by sessionViewModel.user.collectAsState()
    val userOrders by pemesananViewModel.pemesanan.collectAsState()
    val progressList by progressViewModel.progress.collectAsState()

    // Load pemesanan for user
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 20)
            progressViewModel.loadProgressForUser(id, limit = 20)
        }
    }

    // Function to check status and navigate
    fun handleOrderNavigation() {
        val latestPendingOrder = userOrders.firstOrNull { order ->
            order.status_pengerjaan?.lowercase() == "pending" || 
            order.status_pengerjaan?.lowercase() == "proses" ||
            order.status_pengerjaan?.lowercase() == "waiting for payment"
        }

        if (latestPendingOrder != null) {
            onNavigate(Screen.OrderDetail.route)
        } else {
            onNavigate(Screen.Order.route)
        }
    }
    
    // Cek apakah ada order dengan status "waiting for payment"
    val waitingPaymentOrder = userOrders.firstOrNull { order ->
        order.status_pengerjaan?.lowercase() == "waiting for payment" ||
        order.status_pengerjaan?.lowercase() == "waiting to payment"
    }

    val seatDesigns = products.map { p ->
        SeatDesign(
            id = p.product_id,
            name = p.nama_layanan,
            price = p.harga?.toInt() ?: 0,
            imageResId = R.drawable.black,
            badge = "New",
            badgeColor = PrimaryBlue
        )
    }

    // Filter progress dengan status pending, proses, atau waiting for payment
    val activeProgressOrders = progressList.filter { progress ->
        val status = progress.t_pemesanan?.status_pengerjaan?.lowercase()
        status == "pending" || status == "proses" || status == "waiting for payment" || status == "waiting to payment"
    }

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
                            .clickable { /* Notification */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Quick Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("5", "Completed", modifier = Modifier.weight(1f))
                    StatCard("1", "Active", modifier = Modifier.weight(1f))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Active Order
            if (activeProgressOrders.isNotEmpty()) {
                Text(
                    text = "Active Order",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                activeProgressOrders.forEach { progress ->
                    val pesanan = progress.t_pemesanan
                    if (pesanan != null) {
                        val serviceName = pesanan.m_product_layanan?.nama_layanan ?: "Service"
                        val orderId = "ORD-${pesanan.pesanan_id}"
                        val status = pesanan.status_pengerjaan ?: "Unknown"
                        val progressPercentage = progress.presentase_progress
                        val estimatedTime = pesanan.estimasi_selesai ?: "TBD"
                        
                        CustomCardWithBorder(
                            onClick = { 
                                when (status.lowercase()) {
                                    "waiting for payment", "waiting to payment" -> {
                                        onNavigate(Screen.Payment.route)
                                    }
                                    else -> {
                                        onNavigate(Screen.OrderDetail.route)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = orderId,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = serviceName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BackgroundGray
                                ) {
                                    Text(
                                        text = status,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 12.sp,
                                        color = PrimaryBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progress",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$progressPercentage%",
                                    fontSize = 12.sp,
                                    color = PrimaryBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = progressPercentage / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = PrimaryBlue,
                                trackColor = Gray200
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (estimatedTime != "TBD") "Est. selesai: $estimatedTime" else "Estimasi: $estimatedTime",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                            if (progress.keterangan_status != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = progress.keterangan_status,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }



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
                items(seatDesigns) { design ->
                    SeatDesignCard(
                        design = design,
                        onClick = { 
                            // Navigate to CatalogScreen with selected product
                            onNavigate("${Screen.Catalog.route}/${design.id}")
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
                Image(
                    painter = painterResource(id = design.imageResId),
                    contentDescription = design.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
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

