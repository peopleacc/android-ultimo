package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
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
import coil.compose.AsyncImage
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
    var searchQuery by remember { mutableStateOf("") }

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
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
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
                            text = "Katalog Jok Design",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "Search Jok Design...", color = TextSecondary) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PrimaryBlue) },
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
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Content
        if (selectedProduct != null) {
            // Show single product detail
            Column(
                modifier = Modifier
                    .weight(1f)
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
            val filtered = products.filter { p ->
                p.nama_layanan?.contains(searchQuery, ignoreCase = true) == true ||
                p.deskripsi?.contains(searchQuery, ignoreCase = true) == true
            }
            val displayProducts = filtered
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayProducts) { product ->
                    CatalogGridItemCard(
                        product = product,
                        onOrderClick = {
                            // Navigate to CatalogDesignScreen
                            onNavigate("${Screen.CatalogDesign.route}/${product.product_id}")
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
                        text = "You have placed an order",
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
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Info
            Text(
                text = product.nama_layanan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

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
                text = "Place an order",
                onClick = onOrderClick,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.ShoppingCart
            )
        }
    }
}

@Composable
fun CatalogGridItemCard(
    product: com.ultimo.vehicleapp.model.ProductLayanan,
    onOrderClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shadowElevation = 4.dp,
        color = Color.White,
        onClick = onOrderClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product Image with Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryBlue.copy(alpha = 0.3f), PrimaryBlueLight.copy(alpha = 0.5f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AirlineSeatReclineNormal,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                }
                
                // Bottom gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                            )
                        )
                )
                
                // Premium badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBlue.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Yellow
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Premium",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Product Name
                Text(
                    text = product.nama_layanan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price with icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp), 
                        color = PrimaryBlue.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Rp ${(product.harga ?: 0).toString().replace(Regex("(\\d)(?=(\\d{3})+$)"), "$1.")}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PrimaryBlue
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "View",
                            modifier = Modifier
                                .size(28.dp)
                                .padding(6.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
