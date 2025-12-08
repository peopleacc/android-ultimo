package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.ViewModels.PemesananViewModel
import com.ultimo.vehicleapp.ViewModels.ProductViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    productViewModel: ProductViewModel = viewModel(),
    pemesananViewModel: PemesananViewModel = viewModel(),
    productId: Int? = null
) {
    val products by productViewModel.products.collectAsState()
    val currentUser by sessionViewModel.user.collectAsState()
    val userOrders by pemesananViewModel.pemesanan.collectAsState()
    var showAlreadyOrderedSnackbar by remember { mutableStateOf(false) }

    // Load pemesanan for user
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { id ->
            pemesananViewModel.loadPemesananForUser(id, limit = 20)
        }
    }

    // Check if user has pending order
    val hasPendingOrder = userOrders.any { order ->
        order.status_pengerjaan?.lowercase() == "pending"
    }

    // Get selected product if productId is provided
    val selectedProduct = productId?.let { id ->
        products.find { it.product_id == id }
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
                    text = "Katalog Jok Design",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Content
        if (selectedProduct != null) {
            // Show single product detail
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CatalogItemCard(
                    product = selectedProduct,
                    onOrderClick = {
                        if (hasPendingOrder) {
                            showAlreadyOrderedSnackbar = true
                        } else {
                            // Navigate to ServiceOrderScreen with step 2 and selected product
                            onNavigate("${Screen.Order.route}/2/${selectedProduct.product_id}")
                        }
                    }
                )
            }
        } else {
            // Show all products
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    CatalogItemCard(
                        product = product,
                        onOrderClick = {
                            if (hasPendingOrder) {
                                showAlreadyOrderedSnackbar = true
                            } else {
                                // Navigate to ServiceOrderScreen with step 2 and selected product
                                onNavigate("${Screen.Order.route}/2/${product.product_id}")
                            }
                        }
                    )
                }
            }
        }
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
                        text = "Anda sudah melakukan pemesanan",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogItemCard(
    product: com.ultimo.vehicleapp.model.ProductLayanan,
    onOrderClick: () -> Unit
) {
    CustomCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gray200)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.black),
                    contentDescription = product.nama_layanan,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Info
            Text(
                text = product.nama_layanan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Text(
                    text = product.jenis_kategori,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            if (!product.deskripsi.isNullOrBlank()) {
                Text(
                    text = product.deskripsi ?: "",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Harga",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${(product.harga ?: 0).toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Button
            CustomButton(
                text = "Lakukan Pemesanan",
                onClick = onOrderClick,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.ShoppingCart
            )
        }
    }
}

