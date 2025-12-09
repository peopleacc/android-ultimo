package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ultimo.vehicleapp.ViewModels.ProgressViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.Controller.AllProgressRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*

data class TimelineItem(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    val completed: Boolean,
    val active: Boolean = false,
    val icon: ImageVector
)

data class Order(
    val id: String,
    val service: String,
    val status: String,
    val progress: Int,
    val currentStep: String,
    val estimatedTime: String,
    val startDate: String,
    val estimatedCompletion: String,
    val timeline: List<TimelineItem>
)

@Composable
fun OrderTrackingScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    progressViewModel: ProgressViewModel = viewModel(),
    pemesananViewModel: PemesananViewModel = viewModel()
) {
    val currentUser by sessionViewModel.user.collectAsState()
    
    // Card data from loadProgressForUser
    val progressList by progressViewModel.progress.collectAsState()
    
    // Pemesanan data for pending orders
    val userOrders by pemesananViewModel.pemesanan.collectAsState()
    
    // Timeline data from loadAllProgressForUser (we'll load this separately)
    var allProgressList by remember { mutableStateOf<List<com.ultimo.vehicleapp.model.Progress>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Load card data (loadProgressForUser)
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            progressViewModel.loadProgressForUser(id, limit = 10)
            pemesananViewModel.loadPemesananForUser(id, limit = 20)
        }
    }

    // Load timeline data (loadAllProgressForUser) - load all progress for timeline
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            coroutineScope.launch {
                val dataProgress = AllProgressRepository
                    .getAllProgressUser(id.toString())
                    .reversed()
                    .take(50)
                allProgressList = dataProgress
            }
        }
    }
    
    // Filter pending orders from t_pemesanan (status: pending, waiting for order)
    val pendingOrders = userOrders.filter { order ->
        val status = order.status_pengerjaan?.lowercase()
        status == "pending" || status == "waiting" || status == "waiting for order"
    }

    // Convert Progress to Order for card display
    val orders = progressList.mapNotNull { progress ->
        val pesanan = progress.t_pemesanan ?: return@mapNotNull null
        val serviceName = pesanan.m_product_layanan?.nama_layanan ?: "Service"
        val orderId = "ORD-${pesanan.pesanan_id}"
        val status = pesanan.status_pengerjaan ?: "Unknown"
        val progressPercentage = progress.presentase_progress
        val startDate = pesanan.tanggal_pesan ?: "N/A"
        val estimatedCompletion = pesanan.estimasi_selesai ?: "N/A"
        val keterangan = progress.keterangan_status ?: ""

        // Get timeline from allProgressList based on pesanan_id
        val timelineProgressList = allProgressList.filter { 
            it.t_pemesanan?.pesanan_id == pesanan.pesanan_id 
        }
        
        // Generate timeline from keterangan_status (only use keterangan_status from allProgressList)
        val timeline = generateTimelineFromKeterangan(
            keteranganList = timelineProgressList.mapNotNull { it.keterangan_status },
            startDate = startDate,
            estimatedCompletion = estimatedCompletion,
            progressPercentage = progressPercentage
        )

        // Find current active step
        val currentStep = timeline.find { it.active }?.title ?: timeline.firstOrNull()?.title ?: "Order Confirmed"

        // Calculate estimated remaining time
        val estimatedTime = calculateEstimatedTime(progressPercentage, estimatedCompletion)

        Order(
            id = orderId,
            service = serviceName,
            status = status,
            progress = progressPercentage,
            currentStep = currentStep,
            estimatedTime = estimatedTime,
            startDate = startDate,
            estimatedCompletion = estimatedCompletion,
            timeline = timeline
        )
    }

    var selectedOrderId by remember { mutableStateOf<String?>(null) }

    // Initialize selectedOrderId when orders are loaded
    LaunchedEffect(orders) {
        if (selectedOrderId == null && orders.isNotEmpty()) {
            selectedOrderId = orders.first().id
        }
    }

    val selectedOrder = orders.find { it.id == selectedOrderId } ?: orders.firstOrNull()

    // If no orders (both progress and pending), show empty state
    if (orders.isEmpty() && pendingOrders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundPink)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No orders found",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
        return
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
                        text = "Order Tracking",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Order Info Card - kondisi berdasarkan status
                // Jika ada pending order: tampilkan card dari PemesananController
                // Jika ada proses order: tampilkan card dari ProgressController
                
                if (pendingOrders.isNotEmpty()) {
                    // ========== PENDING ORDER CARD (dari PemesananController) ==========
                    val firstPendingOrder = pendingOrders.first()
                    CustomCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ORD-${firstPendingOrder.pesanan_id}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Order #${firstPendingOrder.pesanan_id}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF8E1) // Light amber
                            ) {
                                Text(
                                    text = firstPendingOrder.status_pengerjaan ?: "Pending",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    color = Color(0xFFF57C00) // Amber
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Status",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Menunggu Konfirmasi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57C00)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Tanggal Pesan",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = firstPendingOrder.tanggal_pesan ?: "N/A",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Harga",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rp ${String.format("%,d", firstPendingOrder.total_estimasi_harga ?: 0)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                } else if (selectedOrder != null) {
                    // ========== PROSES ORDER CARD (dari ProgressController) ==========
                    CustomCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedOrder.id,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedOrder.service,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BackgroundGray
                            ) {
                                Text(
                                    text = selectedOrder.status,
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
                                text = "Overall Progress",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${selectedOrder.progress}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = selectedOrder.progress / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = PrimaryBlue,
                            trackColor = Gray200
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Current Step",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedOrder.currentStep,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Est. Remaining",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedOrder.estimatedTime,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
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

            // ========== PENDING ORDERS SECTION (dari t_pemesanan) ==========
            if (pendingOrders.isNotEmpty()) {
                Text(
                    text = "Pending Orders",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                pendingOrders.forEach { order ->
                    val serviceName = "Order #${order.pesanan_id}"
                    val orderId = "ORD-${order.pesanan_id}"
                    val status = order.status_pengerjaan ?: "Pending"
                    val tanggalPesan = order.tanggal_pesan ?: "N/A"
                    val totalHarga = order.total_estimasi_harga ?: 0
                    
                    CustomCardWithBorder(
                        onClick = { onNavigate(Screen.OrderDetail.route) },
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
                                color = Color(0xFFFFF8E1) // Light yellow/amber background
                            ) {
                                Text(
                                    text = status,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    color = Color(0xFFF57C00) // Amber text
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Tanggal Pesan",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tanggalPesan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rp ${String.format("%,d", totalHarga)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFF57C00)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Menunggu konfirmasi dari admin",
                                fontSize = 12.sp,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ========== PROGRESS TIMELINE SECTION (untuk order yang sudah proses) ==========
            if (selectedOrder != null) {
                Text(
                    text = "Progress Timeline",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                selectedOrder.timeline.forEachIndexed { index, item ->
                    TimelineItemView(item = item, isLast = index == selectedOrder.timeline.size - 1)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Estimated Completion
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Estimated Completion",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedOrder.estimatedCompletion,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItemView(
    item: TimelineItem,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = when {
                    item.completed -> PrimaryBlue
                    item.active -> BackgroundGray
                    else -> Gray200
                },
                border = if (item.active) BorderStroke(2.dp, PrimaryBlue) else null,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = when {
                            item.completed -> Color.White
                            item.active -> PrimaryBlue
                            else -> Gray400
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(if (item.completed) PrimaryBlue else Gray200)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        CustomCardWithBorder(
            borderColor = when {
                item.completed -> BackgroundGray
                item.active -> PrimaryBlue
                else -> Gray200
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (item.completed || item.active) TextPrimary else TextTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = if (item.completed || item.active) TextSecondary else TextTertiary
                    )
                    if (item.active) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Currently processing...",
                                fontSize = 12.sp,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
                Text(
                    text = item.time,
                    fontSize = 12.sp,
                    color = if (item.completed || item.active) PrimaryBlue else TextTertiary
                )
            }
        }
    }
}

// Helper function to generate timeline from keterangan_status
fun generateTimelineFromKeterangan(
    keteranganList: List<String>,
    startDate: String,
    estimatedCompletion: String,
    progressPercentage: Int
): List<TimelineItem> {
    val timeline = mutableListOf<TimelineItem>()
    
    // Order Confirmed - always completed
    timeline.add(
        TimelineItem(
            id = 1,
            title = "Order Confirmed",
            description = "Your order has been confirmed",
            time = startDate,
            completed = true,
            icon = Icons.Default.CheckCircle
        )
    )
    
    // Use keterangan_status to create timeline items
    // Each keterangan_status becomes a timeline item
    var itemId = 2
    keteranganList.forEachIndexed { index, keterangan ->
        if (keterangan.isNotBlank()) {
            val isLastKeterangan = index == keteranganList.size - 1
            val isCompleted = !isLastKeterangan || progressPercentage >= 100
            val isActive = isLastKeterangan && progressPercentage < 100
            
            timeline.add(
                TimelineItem(
                    id = itemId++,
                    title = "Progress Update ${index + 1}",
                    description = keterangan,
                    time = startDate,
                    completed = isCompleted,
                    active = isActive,
                    icon = Icons.Default.Build
                )
            )
        }
    }
    
    // If no keterangan, add default steps based on progress
    if (keteranganList.isEmpty()) {
        // Material Preparation - completed if progress > 20
        timeline.add(
            TimelineItem(
                id = 2,
                title = "Material Preparation",
                description = "Preparing materials for installation",
                time = startDate,
                completed = progressPercentage > 20,
                active = progressPercentage > 20 && progressPercentage <= 40,
                icon = Icons.Default.Inventory
            )
        )
        
        // Installation In Progress - active if progress > 40 and < 80
        timeline.add(
            TimelineItem(
                id = 3,
                title = "Installation In Progress",
                description = "Installing your new seat covers",
                time = startDate,
                completed = progressPercentage > 80,
                active = progressPercentage > 40 && progressPercentage <= 80,
                icon = Icons.Default.Build
            )
        )
        
        // Quality Check - completed if progress > 80
        timeline.add(
            TimelineItem(
                id = 4,
                title = "Quality Check",
                description = "Final inspection and quality assurance",
                time = startDate,
                completed = progressPercentage > 90,
                active = progressPercentage > 80 && progressPercentage < 100,
                icon = Icons.Default.Star
            )
        )
    }
    
    // Ready for Pickup - completed if progress = 100
    timeline.add(
        TimelineItem(
            id = itemId,
            title = "Ready for Pickup",
            description = "Your vehicle is ready",
            time = estimatedCompletion,
            completed = progressPercentage >= 100,
            icon = Icons.Default.CheckCircle
        )
    )
    
    return timeline
}

// Helper function to calculate estimated remaining time
fun calculateEstimatedTime(progress: Int, estimatedCompletion: String): String {
    if (progress >= 100) {
        return "Completed"
    }
    if (estimatedCompletion.isNotEmpty() && estimatedCompletion != "N/A") {
        return "Est. $estimatedCompletion"
    }
    val remaining = 100 - progress
    return when {
        remaining > 75 -> "3-4 days"
        remaining > 50 -> "2-3 days"
        remaining > 25 -> "1-2 days"
        else -> "Less than 1 day"
    }
}

