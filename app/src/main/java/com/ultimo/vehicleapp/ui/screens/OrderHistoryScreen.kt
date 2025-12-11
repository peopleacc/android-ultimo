package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.ViewModels.ProgressViewModel
import com.ultimo.vehicleapp.ViewModels.ProductViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

data class OrderHistoryItem(
    val pesananId: Int,
    val id: String,
    val service: String,
    val date: String,
    val status: String,
    val statusColor: Color,
    val price: Int,
    val paymentMethod: String,
    val paymentStatus: String,
    val estimatedTime: String,
    val progress: Int,
    val completedDate: String = "N/A"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    pemesananViewModel: PemesananViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showActive by remember { mutableStateOf(true) }

    val currentUser by sessionViewModel.user.collectAsState()
    val pemesananList by pemesananViewModel.pemesanan.collectAsState()
    val products by productViewModel.products.collectAsState()
    val progressList by progressViewModel.progress.collectAsState()

    // Load pemesanan for user
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 50)
            progressViewModel.loadProgressForUser(id, limit = 20)
        }
    }

    // Filter only selesai status and convert pemesanan to OrderHistoryItem
    val orders = remember(pemesananList, products) {
        pemesananList
            .filter { pemesanan ->
                val status = pemesanan.status_pengerjaan?.lowercase()?.trim()
                status == "selesai" || status == "completed"
            }
            .mapNotNull { pemesanan ->
                // Get product name from product_id
                val product = products.find { it.product_id == pemesanan.product_id }
                val serviceName = product?.nama_layanan ?: "Service"
                val orderId = "ORD-${pemesanan.pesanan_id}"
                val status = pemesanan.status_pengerjaan ?: "Unknown"
                val date = pemesanan.tanggal_pesan ?: "N/A"
                val totalPrice = pemesanan.total_estimasi_harga ?: 0
                val paymentMethod = pemesanan.metode_pembayaran ?: "N/A"
                val paymentStatus = pemesanan.status_pembayaran ?: "Unpaid"
                val estimatedTime = pemesanan.estimasi_selesai ?: "N/A"

                // Determine status color
                val statusColor = when (status.lowercase()) {
                    "completed", "selesai" -> SuccessGreen
                    "in progress", "proses" -> InfoBlue
                    "pending" -> PrimaryBlue
                    else -> TextSecondary
                }

                OrderHistoryItem(
                    pesananId = pemesanan.pesanan_id,
                    id = orderId,
                    service = serviceName,
                    date = date,
                    status = status,
                    statusColor = statusColor,
                    price = totalPrice,
                    paymentMethod = paymentMethod,
                    paymentStatus = paymentStatus,
                    estimatedTime = estimatedTime,
                    progress = 100,
                    completedDate = pemesanan.estimasi_selesai ?: date
                )
            }
    }

    // Since we only show completed orders, filter only by search
    val filteredOrders = orders.filter { order ->
        order.id.contains(searchQuery, ignoreCase = true) ||
        order.service.contains(searchQuery, ignoreCase = true)
    }

    // State for showing completed order detail dialog
    var selectedCompletedOrder by remember { mutableStateOf<OrderHistoryItem?>(null) }

    // Pull to refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
                        text = "Order History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search orders...",
                            color = TextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PrimaryBlue)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = PrimaryBlue,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { /* Search action */ })
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        FilterChip(
                            selected = showActive,
                            onClick = { showActive = true },
                            label = { Text(text = "Active Orders") },
                            leadingIcon = if (showActive) {
                                { Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null) }
                            } else null
                        )
                        FilterChip(
                            selected = !showActive,
                            onClick = { showActive = false },
                            label = { Text(text = "Completed Orders") },
                            leadingIcon = if (!showActive) {
                                { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null) }
                            } else null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    currentUser?.id?.let { id ->
                        pemesananViewModel.loadPemesananForUser(id)
                        progressViewModel.loadProgressForUser(id)
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

            val pendingWaitingOrders = pemesananList.filter { order ->
                val status = order.status_pengerjaan?.lowercase()
                status == "pending" || status == "waiting" || status == "waiting for order" || status == "waiting for payment" || status == "waiting to payment"
            }
            val prosesProgressOrders = progressList.filter { progress ->
                val status = progress.t_pemesanan?.status_pengerjaan?.lowercase()
                status == "proses"
            }
            val menungguPembayaranOrders = pemesananList.filter { order ->
                val status = order.status_pengerjaan?.lowercase()
                status == "menunggu pembayaran"
            }
            val hasActiveOrders = pendingWaitingOrders.isNotEmpty() || prosesProgressOrders.isNotEmpty() || menungguPembayaranOrders.isNotEmpty()

            if (showActive && hasActiveOrders) {
                Text(
                    text = "Active Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                pendingWaitingOrders.forEach { order ->
                    val serviceName = "Order #${order.pesanan_id}"
                    val orderId = "ORD-${order.pesanan_id}"
                    val status = order.status_pengerjaan ?: "Unknown"
                    val progressPercentage = 0
                    val estimatedTime = order.estimasi_selesai ?: "TBD"

                    CustomCardWithBorder(
                        onClick = {
                            when (status.lowercase()) {
                                "waiting for payment", "waiting to payment" -> onNavigate(Screen.Payment.route)
                                else -> onNavigate(Screen.OrderDetail.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = orderId, fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = serviceName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = BackgroundGray) {
                                Text(text = status, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = PrimaryBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Progress", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "$progressPercentage%", fontSize = 12.sp, color = PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = progressPercentage / 100f,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = PrimaryBlue,
                            trackColor = Gray200
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (estimatedTime != "TBD") "Est. selesai: $estimatedTime" else "Menunggu konfirmasi", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                prosesProgressOrders.forEach { progress ->
                    val pesanan = progress.t_pemesanan
                    if (pesanan != null) {
                        val serviceName = pesanan.m_product_layanan?.nama_layanan ?: "Service"
                        val orderId = "ORD-${pesanan.pesanan_id}"
                        val status = pesanan.status_pengerjaan ?: "Unknown"
                        val progressPercentage = progress.presentase_progress
                        val estimatedTime = pesanan.estimasi_selesai ?: "TBD"

                        CustomCardWithBorder(
                            onClick = { onNavigate(Screen.OrderDetail.route) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = orderId, fontSize = 12.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = serviceName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                }
                                Surface(shape = RoundedCornerShape(8.dp), color = BackgroundGray) {
                                    Text(text = status, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = PrimaryBlue)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Progress", fontSize = 12.sp, color = TextSecondary)
                                Text(text = "$progressPercentage%", fontSize = 12.sp, color = PrimaryBlue)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = progressPercentage / 100f,
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = PrimaryBlue,
                                trackColor = Gray200
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = if (estimatedTime != "TBD") "Est. selesai: $estimatedTime" else "Estimasi: $estimatedTime", fontSize = 12.sp, color = TextSecondary)
                            }
                            if (progress.keterangan_status != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = progress.keterangan_status, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                menungguPembayaranOrders.forEach { order ->
                    val serviceName = "Order #${order.pesanan_id}"
                    val orderId = "ORD-${order.pesanan_id}"
                    val status = order.status_pengerjaan ?: "Menunggu Pembayaran"
                    val totalHarga = order.total_estimasi_harga ?: 0

                    CustomCardWithBorder(
                        onClick = { onNavigate(Screen.Payment.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = orderId, fontSize = 12.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = serviceName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF3E0)) {
                                Text(text = status, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, color = Color(0xFFE65100))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total Pembayaran", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Rp ${String.format("%,d", totalHarga)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFE65100))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Klik untuk melakukan pembayaran", fontSize = 12.sp, color = Color(0xFFE65100))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Show section title for completed orders
            if (!showActive && filteredOrders.isNotEmpty()) {
                Text(
                    text = "Completed Orders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Orders List
            if (!showActive && filteredOrders.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(80.dp))
                    Surface(
                        shape = CircleShape,
                        color = BackgroundGray,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = PrimaryBlue
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No orders found",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Try adjusting your search or filter",
                        fontSize = 14.sp,
                        color = TextTertiary
                    )
                }
            } else if (!showActive) {
                filteredOrders
                    .sortedByDescending { it.date }
                    .forEach { order ->
                        OrderHistoryCard(
                            order = order,
                            onClick = { selectedCompletedOrder = order }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
            }
            }
        }
    }

    // Show completed order detail dialog
    selectedCompletedOrder?.let { order ->
        CompletedOrderDetailDialog(
            order = order,
            onDismiss = { selectedCompletedOrder = null }
        )
    }
}

@Composable
fun OrderHistoryCard(
    order: OrderHistoryItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shadowElevation = 4.dp,
        color = Color.White,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row with Order ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Order ID with icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryBlue.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(6.dp),
                                tint = PrimaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = order.id,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = order.service,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = order.statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = order.statusColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = order.statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Date Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.date,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Divider with gradient effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Gray200,
                                Gray200,
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Payment and Price Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Payment Method
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (order.paymentMethod == "QRIS") PrimaryBlue.copy(alpha = 0.1f) else SuccessGreen.copy(alpha = 0.1f)
                    ) {
                        Icon(
                            imageVector = if (order.paymentMethod == "QRIS") Icons.Default.QrCode else Icons.Default.AttachMoney,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .padding(4.dp),
                            tint = if (order.paymentMethod == "QRIS") PrimaryBlue else SuccessGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Pembayaran",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = order.paymentMethod,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
                
                // Total Price
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Rp ${order.price.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // View Details Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BackgroundGray
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lihat Detail",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedOrderDetailDialog(
    order: OrderHistoryItem,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Tutup", fontWeight = FontWeight.Medium)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessGreen.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Pesanan Selesai",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(
                        text = order.id,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Service Info
                CompletedDetailRow(
                    icon = Icons.Default.Build,
                    label = "Layanan",
                    value = order.service,
                    iconColor = PrimaryBlue
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Order Date
                CompletedDetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Tanggal Pemesanan",
                    value = order.date,
                    iconColor = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Completion Date
                CompletedDetailRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Tanggal Selesai",
                    value = order.completedDate,
                    iconColor = SuccessGreen
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Gray200)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Payment Info
                CompletedDetailRow(
                    icon = if (order.paymentMethod == "QRIS") Icons.Default.QrCode else Icons.Default.AttachMoney,
                    label = "Metode Pembayaran",
                    value = order.paymentMethod,
                    iconColor = if (order.paymentMethod == "QRIS") PrimaryBlue else SuccessGreen
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Payment Status
                CompletedDetailRow(
                    icon = Icons.Default.Payment,
                    label = "Status Pembayaran",
                    value = order.paymentStatus,
                    iconColor = if (order.paymentStatus.lowercase().contains("paid") || order.paymentStatus.lowercase().contains("lunas")) SuccessGreen else Color(0xFFE65100)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Total Price Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Rp ${order.price.toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Thank you message
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Terima kasih telah menggunakan layanan kami!",
                            fontSize = 13.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}

@Composable
fun CompletedDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier
                    .size(28.dp)
                    .padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}
