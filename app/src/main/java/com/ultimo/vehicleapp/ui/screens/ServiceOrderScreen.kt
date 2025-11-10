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
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*

data class Design(
    val id: String,
    val name: String,
    val price: Int,
    val imageResId: Int,
    val description: String
)

data class Material(
    val id: String,
    val name: String,
    val quality: String
)

data class ServiceType(
    val id: String,
    val name: String,
    val duration: String,
    val price: Int
)

@Composable
fun ServiceOrderScreen(
    onNavigate: (String) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedDesign by remember { mutableStateOf("leather-premium") }
    var selectedMaterial by remember { mutableStateOf("genuine-leather") }
    var selectedService by remember { mutableStateOf("standard") }
    var notes by remember { mutableStateOf("") }

    val designs = listOf(
        Design(
            "leather-premium", 
            "Premium Leather Black", 
            3000000, 
            R.drawable.black,
            "Material kulit premium atau kulit sintetis berkualitas tinggi dengan finishing hitam pekat, kesan mewah dan elegan. Warna hitam membuat interior lebih netral dan mudah dipadankan dengan warna mobil apa pun."
        ),
        Design(
            "sports-racing", 
            "Sporty Red Accent", 
            4000000, 
            R.drawable.red,
            "Desain sporty dengan aksen warna merah (misalnya jahitan merah, panel merah di sisi jok) yang memberi kesan aktif. Material bisa kulit atau kulit sintetis, dengan kombinasi hitam/merah memberikan kontras visual yang kuat."
        ),
        Design(
            "luxury-comfort", 
            "Luxury Beige Supreme", 
            5000000, 
            R.drawable.beige,
            "Warna beige perpaduan (krem muda / sand / cokelat terang) dengan kesan sangat mewah dan cerah memberi nuansa \"luxury\" dan interior yang terbuka. \"Supreme\" di sini bisa berarti material dan finishing top‑tier: kulit asli atau premium, mungkin dengan aksen jahitan, bordir, dan bantalan ekstra."
        ),
        Design(
            "racing-carbon", 
            "Racing Carbon Style", 
            7000000, 
            R.drawable.racing,
            "Desain highly sporty dengan tampilan \"carbon fibre\" atau motif carbon, sering dengan shell model \"bucket seat\" atau sporty bucket yang mendukung stabilitas saat menikung. Bisa menggunakan rangka atau shell karbon (atau motif karbon), bantalan tipis, posisi duduk agak rendah/menyelam untuk kesan balap."
        )
    )

    val materials = listOf(
        Material("genuine-leather", "Genuine Leather", "Premium"),
        Material("synthetic-leather", "Synthetic Leather", "Standard"),
        Material("premium-fabric", "Premium Fabric", "High"),
        Material("suede", "Suede Material", "Luxury")
    )

    val serviceTypes = listOf(
        ServiceType("standard", "Standard", "3-4 days", 0),
        ServiceType("express", "Express", "1-2 days", 500000),
        ServiceType("same-day", "Same Day", "6-8 hours", 1000000)
    )

    val selectedDesignData = designs.find { it.id == selectedDesign }
    val selectedMaterialData = materials.find { it.id == selectedMaterial }
    val selectedServiceData = serviceTypes.find { it.id == selectedService }
    val totalPrice = (selectedDesignData?.price ?: 0) + (selectedServiceData?.price ?: 0)
    val estimatedTime = selectedServiceData?.duration ?: ""

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

                // Progress Steps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..4).forEach { s ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (s <= step) Color.White else Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (s < step) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = s.toString(),
                                            color = if (s <= step) PrimaryBlue else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            if (s < 4) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(2.dp)
                                        .background(
                                            if (s < step) Color.White else Color.White.copy(alpha = 0.3f)
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
                3 -> Step3Content(
                    serviceTypes, 
                    selectedService,
                    onServiceSelected = { selectedService = it },
                    onContinue = { step++ }
                )
                4 -> Step4Content(
                    selectedDesignData, 
                    selectedMaterialData, 
                    selectedServiceData, 
                    notes, 
                    estimatedTime,
                    onNotesChange = { notes = it },
                    onContinue = { onNavigate(Screen.Payment.route) }
                )
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
                            text = estimatedTime,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (step < 4) {
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
                            text = if (step < 4) "Continue" else "Confirm Order",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (step < 4) Icons.Default.ChevronRight else Icons.Default.Check,
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
    selectedDesign: String,
    onDesignSelected: (String) -> Unit,
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
        enabled = selectedDesign.isNotEmpty(),
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
    materials: List<Material>,
    selectedMaterial: String,
    onMaterialSelected: (String) -> Unit,
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
                            text = "Quality: ${material.quality}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
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
        enabled = selectedMaterial.isNotEmpty(),
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
fun Step3Content(
    serviceTypes: List<ServiceType>,
    selectedService: String,
    onServiceSelected: (String) -> Unit,
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
            text = "Service Type",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }

    serviceTypes.forEach { service ->
        CustomCardWithBorder(
            onClick = { onServiceSelected(service.id) },
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
                        selected = selectedService == service.id,
                        onClick = { onServiceSelected(service.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = service.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Text(
                            text = service.duration,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (service.price > 0) {
                            Text(
                                text = "+Rp ${service.price.toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                                fontSize = 12.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                if (selectedService == service.id) {
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
        enabled = selectedService.isNotEmpty(),
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
    selectedMaterial: Material?,
    selectedService: ServiceType?,
    notes: String,
    estimatedTime: String,
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
            text = "Additional Notes",
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
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Service Type:", fontSize = 14.sp, color = TextSecondary)
            Text(
                text = selectedService?.name ?: "",
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Estimated Time:", fontSize = 14.sp, color = TextSecondary)
            Text(
                text = estimatedTime,
                fontSize = 14.sp,
                color = PrimaryBlue
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
                text = "Rp ${((selectedDesign?.price ?: 0) + (selectedService?.price ?: 0)).toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
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

