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
import androidx.compose.ui.text.font.FontStyle
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
import kotlinx.coroutines.delay
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

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

// Data class untuk menggabungkan semua jenis order yang bisa dipilih
data class TrackableOrder(
    val id: String,
    val pesananId: Int,
    val serviceName: String,
    val status: String,
    val statusType: OrderStatusType, // pending, proses, menunggu_pembayaran
    val progress: Int,
    val tanggalPesan: String,
    val estimasiSelesai: String,
    val totalHarga: Int,
    val timeline: List<TimelineItem>
)

enum class OrderStatusType {
    PENDING,
    MENUNGGU_PEMBAYARAN,
    PROSES
}

@OptIn(ExperimentalMaterial3Api::class)
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

    // Pull to refresh state
    var isRefreshing by remember { mutableStateOf(false) }

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

    // ========== UNIFIED TRACKABLE ORDER LIST ==========
    // Menggabungkan semua order dari berbagai sumber menjadi satu list yang bisa dipilih
    
    val allTrackableOrders = mutableListOf<TrackableOrder>()
    
    // 1. Tambahkan Pending Orders
    pendingWaitingOrders.forEach { order ->
        val timeline = listOf(
            TimelineItem(
                id = 1,
                title = "Waiting for an order",
                description = "Your order is awaiting confirmation",
                time = order.tanggal_pesan ?: "N/A",
                completed = false,
                active = true,
                icon = Icons.Default.AccessTime
            )
        )
        allTrackableOrders.add(
            TrackableOrder(
                id = "ORD-${order.pesanan_id}",
                pesananId = order.pesanan_id,
                serviceName = "Order #${order.pesanan_id}",
                status = "Pending",
                statusType = OrderStatusType.PENDING,
                progress = 0,
                tanggalPesan = order.tanggal_pesan ?: "N/A",
                estimasiSelesai = order.estimasi_selesai ?: "N/A",
                totalHarga = order.total_estimasi_harga ?: 0,
                timeline = timeline
            )
        )
    }
    
    // 2. Tambahkan Menunggu Pembayaran Orders
    menungguPembayaranOrders.forEach { order ->
        val timeline = listOf(
            TimelineItem(
                id = 1,
                title = "Waiting for payment",
                description = "Order awaiting payment",
                time = order.tanggal_pesan ?: "N/A",
                completed = false,
                active = true,
                icon = Icons.Default.Payment
            )
        )
        allTrackableOrders.add(
            TrackableOrder(
                id = "ORD-${order.pesanan_id}",
                pesananId = order.pesanan_id,
                serviceName = "Order #${order.pesanan_id}",
                status = "Menunggu Pembayaran",
                statusType = OrderStatusType.MENUNGGU_PEMBAYARAN,
                progress = 10,
                tanggalPesan = order.tanggal_pesan ?: "N/A",
                estimasiSelesai = order.estimasi_selesai ?: "N/A",
                totalHarga = order.total_estimasi_harga ?: 0,
                timeline = timeline
            )
        )
    }
    
    // 3. Tambahkan Proses Orders (dari ProgressController dengan timeline lengkap)
    prosesProgressOrders.forEach { progress ->
        val pesanan = progress.t_pemesanan ?: return@forEach
        val startDate = pesanan.tanggal_pesan ?: "N/A"
        val estimatedCompletion = pesanan.estimasi_selesai ?: "N/A"
        val progressPercentage = progress.presentase_progress
        
        // Get timeline from allProgressList based on pesanan_id
        val timelineProgressList = allProgressList.filter { 
            it.t_pemesanan?.pesanan_id == pesanan.pesanan_id 
        }
        
        // Generate timeline from keterangan_status
        val timeline = generateTimelineFromKeterangan(
            keteranganList = timelineProgressList.mapNotNull { it.keterangan_status },
            startDate = startDate,
            estimatedCompletion = estimatedCompletion,
            progressPercentage = progressPercentage
        )
        
        allTrackableOrders.add(
            TrackableOrder(
                id = "ORD-${pesanan.pesanan_id}",
                pesananId = pesanan.pesanan_id ?: 0,
                serviceName = pesanan.m_product_layanan?.nama_layanan ?: "Service",
                status = "Proses",
                statusType = OrderStatusType.PROSES,
                progress = progressPercentage,
                tanggalPesan = startDate,
                estimasiSelesai = estimatedCompletion,
                totalHarga = pesanan.total_estimasi_harga ?: 0,
                timeline = timeline
            )
        )
    }

    // Convert Progress to Order for card display (keep for backward compatibility)
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

    var selectedTrackableOrderId by remember { mutableStateOf<String?>(null) }

    // Initialize selectedTrackableOrderId with first PROSES order when orders are loaded
    LaunchedEffect(allTrackableOrders.size) {
        if (selectedTrackableOrderId == null && allTrackableOrders.isNotEmpty()) {
            // Prioritaskan order dengan status PROSES untuk tracking timeline
            val prosesOrder = allTrackableOrders.firstOrNull { it.statusType == OrderStatusType.PROSES }
            selectedTrackableOrderId = prosesOrder?.id ?: allTrackableOrders.first().id
        }
    }

    val selectedTrackableOrder = allTrackableOrders.find { it.id == selectedTrackableOrderId } ?: allTrackableOrders.firstOrNull()

    // If no orders (both progress and pending), show empty state
    if (orders.isEmpty() && pendingWaitingOrders.isEmpty() && menungguPembayaranOrders.isEmpty()) {
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

                // ========== DROPDOWN PILIH ORDER (HANYA PROSES) ==========
                // Tampilkan dropdown jika ada lebih dari 1 order dengan status proses
                val prosesOrders = allTrackableOrders.filter { it.statusType == OrderStatusType.PROSES }
                if (prosesOrders.size > 1) {
                    Text(
                        text = "Pilih Order untuk Tracking",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedTrackableOrder?.let { 
                                if (it.statusType == OrderStatusType.PROSES) "${it.id} • ${it.serviceName}" else ""
                            } ?: "",
                            onValueChange = { },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // Hanya tampilkan order dengan status PROSES
                            prosesOrders.forEach { order ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(
                                                text = "${order.id} • ${order.serviceName}",
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "Progress: ${order.progress}%",
                                                fontSize = 12.sp,
                                                color = PrimaryBlue
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedTrackableOrderId = order.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ========== ORDER INFO CARD (berdasarkan order yang dipilih) ==========
                selectedTrackableOrder?.let { order ->
                    CustomCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = if (order.statusType == OrderStatusType.MENUNGGU_PEMBAYARAN) {
                            { onNavigate("${Screen.Payment.route}/${order.pesananId}") }
                        } else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.id,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = order.serviceName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (order.statusType) {
                                    OrderStatusType.PENDING -> Color(0xFFFFF8E1)
                                    OrderStatusType.MENUNGGU_PEMBAYARAN -> Color(0xFFFFF3E0)
                                    OrderStatusType.PROSES -> BackgroundGray
                                }
                            ) {
                                Text(
                                    text = order.status,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    color = when (order.statusType) {
                                        OrderStatusType.PENDING -> Color(0xFFF57C00)
                                        OrderStatusType.MENUNGGU_PEMBAYARAN -> Color(0xFFFF9800)
                                        OrderStatusType.PROSES -> PrimaryBlue
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Tampilan berbeda berdasarkan status
                        when (order.statusType) {
                            OrderStatusType.PENDING, OrderStatusType.MENUNGGU_PEMBAYARAN -> {
                                // Status info untuk pending/menunggu pembayaran
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
                                        text = when (order.statusType) {
                                            OrderStatusType.PENDING -> "Menunggu Konfirmasi"
                                            OrderStatusType.MENUNGGU_PEMBAYARAN -> "Menunggu Pembayaran"
                                            else -> order.status
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.statusType) {
                                            OrderStatusType.PENDING -> Color(0xFFF57C00)
                                            OrderStatusType.MENUNGGU_PEMBAYARAN -> Color(0xFFFF9800)
                                            else -> PrimaryBlue
                                        }
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
                                            text = "Order Date",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = order.tanggalPesan,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Total Price",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Rp ${String.format("%,d", order.totalHarga)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                                
                                // Hint untuk klik ke Payment Screen
                                if (order.statusType == OrderStatusType.MENUNGGU_PEMBAYARAN) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PrimaryBlue.copy(alpha = 0.1f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Payment,
                                                contentDescription = null,
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Tap untuk upload bukti pembayaran",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = PrimaryBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = PrimaryBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            OrderStatusType.PROSES -> {
                                // Progress bar untuk order yang sedang diproses
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
                                        text = "${order.progress}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = order.progress / 100f,
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
                                            text = order.timeline.find { it.active }?.title ?: "Processing",
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
                                            text = calculateEstimatedTime(order.progress, order.estimasiSelesai),
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
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    currentUser?.id?.let { id ->
                        progressViewModel.loadProgressForUser(id, limit = 10)
                        pemesananViewModel.loadPemesananForUser(id, limit = 20)
                        // Refresh allProgressList
                        val dataProgress = AllProgressRepository
                            .getAllProgressUser(id.toString())
                            .reversed()
                            .take(50)
                        allProgressList = dataProgress
                    }
                    delay(1000) // Short delay for visual feedback
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

            // ========== PROGRESS TIMELINE SECTION ==========
            // Timeline hanya ditampilkan untuk order dengan status PROSES
            selectedTrackableOrder?.let { order ->
                if (order.statusType == OrderStatusType.PROSES) {
                    // Tampilkan Progress Timeline untuk order PROSES
                    Text(
                        text = "Progress Timeline",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Tampilkan timeline dari order yang dipilih
                    order.timeline.forEachIndexed { index, item ->
                        TimelineItemView(item = item, isLast = index == order.timeline.size - 1)
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
                                    text = order.estimasiSelesai,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                } else {
                    // Untuk order yang bukan PROSES, tampilkan pesan info
                    CustomCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Progress Timeline",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when (order.statusType) {
                                    OrderStatusType.PENDING -> "The order is still awaiting confirmation. The progress timeline will appear while the order is being processed."
                                    OrderStatusType.MENUNGGU_PEMBAYARAN -> "Order is awaiting payment. The progress timeline will appear once the payment is confirmed."
                                    else -> "Progress is not available yet."
                                },
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
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

