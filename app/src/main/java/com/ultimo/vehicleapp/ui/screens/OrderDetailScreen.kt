package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.theme.*
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.model.pemesanan
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OrderDetailScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    pemesananViewModel: PemesananViewModel = viewModel()
) {
    val currentUser by sessionViewModel.user.collectAsState()
    val userOrders by pemesananViewModel.pemesanan.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    var showCancelDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }
    
    // Load pemesanan for user
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 20)
        }
    }
    
    // Get the latest order with pending or proses status
    val latestPendingOrder = userOrders.firstOrNull { order ->
        order.status_pengerjaan?.lowercase() == "pending" || 
        order.status_pengerjaan?.lowercase() == "proses"
    }
    
    // Handle cancel order
// Fixed function in OrderDetailScreen.kt
    fun handleCancelOrder() {
        // Create local, immutable copies of the order and user ID
        val orderToCancel = latestPendingOrder
        val userId = currentUser?.id

        // Check if the local copies are valid
        if (orderToCancel != null && userId != null) {
            isDeleting = true
            coroutineScope.launch {
                try {
                    pemesananViewModel.deletePemesanan(
                        pesananId = orderToCancel.pesanan_id,
                        userId = userId // Use the safe, local variable
                    )
                    showSuccessMessage = true
                    showCancelDialog = false
                    // Navigate to home after successful deletion
                    kotlinx.coroutines.delay(1500)
                    onNavigate(Screen.Home.route)
                } catch (e: Exception) {
                    showErrorMessage = true
                } finally {
                    isDeleting = false
                }
            }
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
                    text = "Order Detail",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            if (latestPendingOrder != null) {
                OrderDetailContent(
                    order = latestPendingOrder,
                    onPay = { onNavigate(Screen.Payment.route) }
                )
                
                // Tombol untuk melihat tracking order
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onNavigate(Screen.Tracking.route) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lacak Pesanan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Tombol Membatalkan Pesanan hanya muncul saat status pending
                if (latestPendingOrder.status_pengerjaan?.lowercase() == "pending") {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            showCancelDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red),
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Red,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Membatalkan...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Membatalkan Pesanan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            } else {
                CustomCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Active Order",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "You don't have any pending or in-progress orders",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigate(Screen.Order.route) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Order New Service")
                        }
                    }
                }
            }
        }
    }
    
    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isDeleting) {
                    showCancelDialog = false
                }
            },
            title = {
                Text(
                    text = "Batalkan Pesanan?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin membatalkan pesanan ini? Tindakan ini tidak dapat dibatalkan.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { handleCancelOrder() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Ya, Batalkan")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false },
                    enabled = !isDeleting
                ) {
                    Text("Tidak")
                }
            }
        )
    }
    
    // Success Message
    if (showSuccessMessage) {
        LaunchedEffect(showSuccessMessage) {
            kotlinx.coroutines.delay(2000)
            showSuccessMessage = false
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = SuccessGreen,
                contentColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Pesanan berhasil dibatalkan",
                        color = Color.White
                    )
                }
            }
        }
    }
    
    // Error Message
    if (showErrorMessage) {
        LaunchedEffect(showErrorMessage) {
            kotlinx.coroutines.delay(3000)
            showErrorMessage = false
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Gagal membatalkan pesanan. Silakan coba lagi.",
                        color = Color.White
                    )
                }
            }
        }
    }
}

