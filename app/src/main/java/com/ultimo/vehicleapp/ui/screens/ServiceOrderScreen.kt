package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*
import com.ultimo.vehicleapp.ViewModels.ProductViewModel
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.ViewModels.MaterialRepository
import com.ultimo.vehicleapp.model.Material_List
import kotlinx.coroutines.coroutineScope
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.ultimo.vehicleapp.Controller.PemesananInsertRepository
import com.ultimo.vehicleapp.Controller.PemesananRepository
import com.ultimo.vehicleapp.model.pemesanan
import com.ultimo.vehicleapp.model.PemesananInsert

data class Design(
    val id: Int,
    val name: String,
    val price: Int,
    val imageResId: Int,
    val description: String
)

data class MaterialUI(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double
)

@Composable
fun ServiceOrderScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel,
    productViewModel: ProductViewModel = viewModel(),
    materialViewModel: MaterialRepository = viewModel()
) {
    var step by remember { mutableStateOf(1) }
    var selectedDesign by remember { mutableStateOf(0) }
    var selectedMaterial by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }

    val products by productViewModel.products.collectAsState()
    val materialList by materialViewModel.Material_List.collectAsState()

    val currentUser by sessionViewModel.user.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var currentOrder by remember { mutableStateOf<pemesanan?>(null) }

    val designs = products.map { p ->
        Design(
            id = p.product_id,
            name = p.nama_layanan,
            price = p.harga.toInt(),
            imageResId = R.drawable.black,
            description = "Premium design option"
        )
    }

    val materials = materialList.map { m ->
        MaterialUI(
            id = m.bahan_id,
            name = m.nama_bahan,
            description = m.deskripsi,
            price = m.harga_per_unit
        )
    }

    val selectedDesignData = designs.find { it.id == selectedDesign }
    val selectedMaterialData = materials.find { it.id == selectedMaterial }
    val totalPrice = (selectedDesignData?.price ?: 0) + (selectedMaterialData?.price?.toInt() ?: 0)
    val statuspengerjaan = "pending"

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
                    IconButton(onClick = {
                        if (step > 1) step-- else onNavigate(Screen.Home.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New Service Order",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Progress Steps: 3 tahapan (Design -> Material -> Review)
                val stepLabels = listOf(1, 2, 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    stepLabels.forEachIndexed { index, label ->
                        val sIndex = index + 1 // posisi logis (1..3)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (sIndex <= step) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (sIndex < step) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = label.toString(),
                                            color = if (sIndex <= step) PrimaryBlue else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            if (index < stepLabels.size - 1) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(2.dp)
                                        .background(
                                            if (sIndex < step) Color.White else Color.White.copy(alpha = 0.3f)
                                        )
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
            // If there's an existing order (just inserted), show its detail
            if (currentOrder != null) {
                OrderDetailContent(
                    order = currentOrder!!,
                    onPay = { onNavigate(Screen.Payment.route) }
                )
            } else {
                // 3 posisi logis: 1 (Design) -> 2 (Material) -> 3 (Review) -> Navigate to Payment
                when (step) {
                    1 -> Step1Content(
                        designs,
                        selectedDesign,
                        onDesignSelected = { selectedDesign = it },
                        onContinue = { step++ }
                    )
                    2 -> Step2Content(
                        selectedDesignData,
                        materials,
                        selectedMaterial,
                        onMaterialSelected = { selectedMaterial = it },
                        onContinue = { step++ }
                    )
                    3 -> Step4Content(
                        selectedDesignData,
                        selectedMaterialData,
                        notes,
                        onNotesChange = { notes = it },
                        onContinue = {
                            // Insert order and then load the user's latest order
                            coroutineScope.launch {
                                val userId = currentUser?.id?.toInt() ?: return@launch
                                val currentDate = java.time.LocalDate.now().toString()

                                val data = PemesananInsert(
                                    user_id = userId,
                                    product_id = selectedDesign,
                                    bahan_id = selectedMaterial,
                                    total_estimasi_harga = totalPrice,
                                    status_pengerjaan = statuspengerjaan,
                                    tanggal_pesan = currentDate
                                )

                                val success = PemesananInsertRepository.insertPemesanan(data)
                                if (success) {
                                    val all = PemesananRepository.getAllPemesanan()
                                    currentOrder = all.findLast { it.user_id == userId } ?: all.lastOrNull()
                                    // If status indicates waiting to payment, navigate to payment
                                    if (currentOrder?.status_pengerjaan?.equals("waiting to payment", true) == true) {
                                        onNavigate(Screen.Payment.route)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        // Bottom Action Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BackgroundPink,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Price",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Rp ${totalPrice.toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Est. Time",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "On Order",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            onNavigate(Screen.Payment.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0D1282),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (step < 3) "Continue" else "Confirm Order",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (step < 3) Icons.Default.ChevronRight else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Step1Content(
    designs: List<Design>,
    selectedDesign: Int,
    onDesignSelected: (Int) -> Unit,
    onContinue: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF7886C7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Choose Design",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        designs.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { design ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (selectedDesign == design.id) 3.dp else 1.dp,
                                color = Color(0xFF7886C7),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onDesignSelected(design.id) }
                    ) {
                        // Background Image
                        Image(
                            painter = painterResource(id = design.imageResId),
                            contentDescription = design.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Overlay Gradient untuk readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                        
                        // Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = design.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rp ${design.price.toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            if (selectedDesign == design.id) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PrimaryBlue
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Continue Button
    Button(
        onClick = onContinue,
        enabled = selectedDesign != 0,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0D1282),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun Step2Content(
    selectedDesign: Design?,
    materials: List<MaterialUI>,
    selectedMaterial: Int,
    onMaterialSelected: (Int) -> Unit,
    onContinue: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF7886C7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Design Details",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }

    selectedDesign?.let { design ->
        CustomCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = design.imageResId),
                    contentDescription = design.name,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = design.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rp ${design.price.toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                        fontSize = 14.sp,
                        color = PrimaryBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Description",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = design.description,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF7886C7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = "Select Material",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }

    materials.forEach { material ->
        CustomCardWithBorder(
            onClick = { onMaterialSelected(material.id) },
            borderColor = Color(0xFF7886C7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedMaterial == material.id,
                        onClick = { onMaterialSelected(material.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = material.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Rp ${material.price.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (material.description != null) {
                            Text(
                                text = material.description,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                if (selectedMaterial == material.id) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Continue Button
    Button(
        onClick = onContinue,
        enabled = selectedMaterial != 0,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0D1282),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun Step4Content(
    selectedDesign: Design?,
    selectedMaterial: MaterialUI?,
    notes: String,
    onNotesChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF7886C7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = "Review Order",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }

    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(bottom = 16.dp),
        placeholder = {
            Text(
                text = "Any special requests or notes for your order...",
                color = Color(0xFF757575),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF7886C7),
            unfocusedBorderColor = Color(0xFF7886C7)
        ),
        maxLines = 5
    )

    CustomCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF7886C7),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Text(
                text = "Order Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Design:", fontSize = 14.sp, color = TextSecondary)
            Text(
                text = selectedDesign?.name ?: "",
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Design Price:", fontSize = 14.sp, color = TextSecondary)
            Text(
                text = "Rp ${(selectedDesign?.price ?: 0).toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Material:", fontSize = 14.sp, color = TextSecondary)
            Text(
                text = selectedMaterial?.name ?: "",
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Material Price:", fontSize = 14.sp, color = TextSecondary)
            Text(
                text = "Rp ${selectedMaterial?.price?.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
        Divider(modifier = Modifier.padding(vertical = 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Price:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Rp ${((selectedDesign?.price ?: 0) + (selectedMaterial?.price?.toInt() ?: 0)).toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Continue Button
    Button(
        onClick = onContinue,
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0D1282),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progres Order",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun OrderDetailContent(
    order: pemesanan,
    onPay: () -> Unit
) {
    CustomCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Order Detail",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Order ID:", color = TextSecondary)
            Text(text = order.pesanan_id.toString(), color = TextPrimary, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Status:", color = TextSecondary)
            Text(text = order.status_pengerjaan.toString(), color = TextPrimary, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total:", color = TextSecondary)
            Text(text = "Rp ${order.total_estimasi_harga}", color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (order.status_pengerjaan.equals("waiting to payment", true)) {
            Button(
                onClick = onPay,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1282), contentColor = Color.White)
            ) {
                Text(text = "Pay Now")
            }
        } else {
            Text(text = "Current status: ${order.status_pengerjaan}", color = TextSecondary)
        }
    }
}

