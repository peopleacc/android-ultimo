package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.ViewModels.ProductViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.Controller.MaterialRepository
import com.ultimo.vehicleapp.model.Material_List
import com.ultimo.vehicleapp.model.ProductLayanan
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogDesignScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    productViewModel: ProductViewModel = viewModel(),
    pemesananViewModel: PemesananViewModel = viewModel(),
    productId: Int
) {
    val products by productViewModel.products.collectAsState()
    val currentUser by sessionViewModel.user.collectAsState()
    val userOrders by pemesananViewModel.pemesanan.collectAsState()
    
    // Material list
    var materials by remember { mutableStateOf<List<Material_List>>(emptyList()) }
    var selectedMaterial by remember { mutableStateOf<Material_List?>(null) }
    var showMaterialDialog by remember { mutableStateOf(false) }
    var showAlreadyOrderedSnackbar by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // Get product by ID
    val product = products.find { it.product_id == productId }
    
    // Load pemesanan for user
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 20)
        }
    }
    
    // Load materials
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                materials = MaterialRepository.getAllMaterial()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
    
    // Check if user has pending order
    val hasPendingOrder = userOrders.any { order ->
        order.status_pengerjaan?.lowercase() == "pending"
    }
    
    // Calculate total price
    val totalPrice = (product?.harga ?: 0) + (selectedMaterial?.harga_per_unit ?: 0)

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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onNavigate(Screen.Catalog.route) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detail Katalog",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        if (product == null) {
            // Loading or product not found
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryBlue)
                } else {
                    Text(
                        text = "Produk tidak ditemukan",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            // Product detail content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Image
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (product.gambar_url.isNullOrEmpty()) Color.White else Gray200)
                    ) {
                        if (!product.gambar_url.isNullOrEmpty()) {
                            AsyncImage(
                                model = product.gambar_url,
                                contentDescription = product.nama_layanan,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.black),
                                placeholder = painterResource(R.drawable.black)
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Gray400
                                )
                            }
                        }
                    }
                }
                
                // Product Info Card
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Product Name
                        Text(
                            text = product.nama_layanan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Price
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryBlue.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Rp ${formatPrice(product.harga ?: 0)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Description
                        Text(
                            text = "Deskripsi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.deskripsi ?: "Tidak ada deskripsi tersedia untuk produk ini.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }
                
                // Material Selection Card
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pilih Material",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                if (selectedMaterial != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = selectedMaterial!!.nama_bahan,
                                        fontSize = 14.sp,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedMaterial != null) SuccessGreen.copy(alpha = 0.1f) else PrimaryBlue.copy(alpha = 0.1f),
                                modifier = Modifier.clickable { showMaterialDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (selectedMaterial != null) Icons.Default.CheckCircle else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (selectedMaterial != null) SuccessGreen else PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (selectedMaterial != null) "Ubah" else "Pilih",
                                        fontSize = 14.sp,
                                        color = if (selectedMaterial != null) SuccessGreen else PrimaryBlue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        if (selectedMaterial != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Harga Material",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Rp ${formatPrice(selectedMaterial!!.harga_per_unit)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
                
                // Price Summary Card
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ringkasan Harga",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Harga Desain",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Rp ${formatPrice(product.harga ?: 0)}",
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        
                        if (selectedMaterial != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Harga Material (${selectedMaterial!!.nama_bahan})",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Rp ${formatPrice(selectedMaterial!!.harga_per_unit)}",
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Estimasi",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Rp ${formatPrice(totalPrice)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Place Order Button
                CustomButton(
                    text = if (selectedMaterial == null) "Pilih Material Terlebih Dahulu" else "Place an Order",
                    onClick = {
                        if (selectedMaterial == null) {
                            showMaterialDialog = true
                        } else if (hasPendingOrder) {
                            showAlreadyOrderedSnackbar = true
                        } else {
                            // Navigate to ServiceOrderScreen with product and material info
                            // Format: order/{step}/{productId}/{materialId}
                            onNavigate("${Screen.Order.route}/1/${product.product_id}/${selectedMaterial!!.bahan_id}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.ShoppingCart,
                    enabled = selectedMaterial != null
                )
            }
        }
    }
    
    // Material Selection Dialog
    if (showMaterialDialog) {
        AlertDialog(
            onDismissRequest = { showMaterialDialog = false },
            title = {
                Text(
                    text = "Pilih Material",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (materials.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    } else {
                        materials.forEach { material ->
                            val isSelected = selectedMaterial?.bahan_id == material.bahan_id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMaterial = material
                                        showMaterialDialog = false
                                    }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) PrimaryBlue else Gray200,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else Color.White
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = material.nama_bahan,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                        if (!material.deskripsi.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = material.deskripsi,
                                                fontSize = 12.sp,
                                                color = TextSecondary,
                                                maxLines = 2
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Rp ${formatPrice(material.harga_per_unit)}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                    
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMaterialDialog = false }) {
                    Text("Tutup", color = PrimaryBlue)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    // Snackbar for already ordered notification
    LaunchedEffect(showAlreadyOrderedSnackbar) {
        if (showAlreadyOrderedSnackbar) {
            kotlinx.coroutines.delay(3000)
            showAlreadyOrderedSnackbar = false
        }
    }

    if (showAlreadyOrderedSnackbar) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Snackbar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = "Anda sudah memiliki pesanan yang sedang diproses",
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Helper function to format price
private fun formatPrice(price: Int): String {
    return price.toString().reversed().chunked(3).joinToString(".").reversed()
}
