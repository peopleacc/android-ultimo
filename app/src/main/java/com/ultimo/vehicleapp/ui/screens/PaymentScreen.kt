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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PaymentScreen(
    onNavigate: (String) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("qris") }
    var orderConfirmed by remember { mutableStateOf(false) }

    val orderDetails = mapOf(
        "id" to "ORD-001",
        "service" to "Premium Leather Installation",
        "design" to "Premium Leather",
        "material" to "Genuine Leather",
        "serviceType" to "Standard",
        "totalPrice" to 2500000,
        "estimatedTime" to "3-4 days"
    )

    if (orderConfirmed) {
        OrderConfirmedScreen(
            orderId = orderDetails["id"] as String,
            onNavigate = { onNavigate(Screen.Home.route) }
        )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate(Screen.Order.route) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Payment",
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

            // Order Summary
            CustomCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Order Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OrderSummaryRow("Order ID", orderDetails["id"] as String)
                Spacer(modifier = Modifier.height(12.dp))
                OrderSummaryRow("Service", orderDetails["service"] as String)
                Spacer(modifier = Modifier.height(12.dp))
                OrderSummaryRow("Design", orderDetails["design"] as String)
                Spacer(modifier = Modifier.height(12.dp))
                OrderSummaryRow("Material", orderDetails["material"] as String)
                Spacer(modifier = Modifier.height(12.dp))
                OrderSummaryRow("Service Type", orderDetails["serviceType"] as String)
                Spacer(modifier = Modifier.height(12.dp))
                OrderSummaryRow("Estimated Time", orderDetails["estimatedTime"] as String, PrimaryBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Amount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Rp ${(orderDetails["totalPrice"] as Int).toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Payment Method",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Payment Methods
            PaymentMethodCard(
                title = "QRIS Payment",
                description = "Scan QR code with any e-wallet",
                icon = Icons.Default.QrCode,
                isSelected = paymentMethod == "qris",
                onClick = { paymentMethod = "qris" }
            )
            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodCard(
                title = "Cash Payment",
                description = "Pay when you pick up your vehicle",
                icon = Icons.Default.AttachMoney,
                isSelected = paymentMethod == "cash",
                onClick = { paymentMethod = "cash" }
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Payment Instructions
            if (paymentMethod == "qris") {
                QRISInstructions()
            } else {
                CashInstructions()
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // Submit Button

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
                Button(
                    onClick = {
                        orderConfirmed = true
                    },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(3.dp, Color(0xFF0D1282), RoundedCornerShape(12.dp)),
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
        }
    }
}

@Composable
fun OrderSummaryRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor
        )
    }
}

@Composable
fun PaymentMethodCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    CustomCardWithBorder(
        onClick = onClick,
        borderColor = if (isSelected) PrimaryBlue else BackgroundGray,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (title.contains("QRIS")) BackgroundGray else SuccessGreen.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (title.contains("QRIS")) PrimaryBlue else SuccessGreen,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QRISInstructions() {
    CustomCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "QRIS Payment Instructions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Gray200,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 16.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp),
                    tint = Gray400
                )
            }
        }
        Column {
            InstructionStep("1. Open your e-wallet app")
            InstructionStep("2. Scan the QR code above")
            InstructionStep("3. Confirm payment amount")
            InstructionStep("4. Complete the transaction")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction ID: TRX-20251102-001",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                IconButton(onClick = { /* Copy */ }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CashInstructions() {
    CustomCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Cash Payment Instructions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SuccessGreen,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Column {
            InstructionStep("• Your order will be confirmed immediately")
            InstructionStep("• Prepare exact amount when picking up")
            InstructionStep("• Payment receipt will be provided on-site")
            InstructionStep("• You can also request an invoice via email")
        }
    }
}

@Composable
fun InstructionStep(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun OrderConfirmedScreen(
    orderId: String,
    onNavigate: () -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = SuccessGreen.copy(alpha = 0.2f),
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SuccessGreen
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Order Confirmed!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your order has been successfully placed.\nWe'll notify you when the work begins.",
                fontSize = 16.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BackgroundGray
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Order ID",
                        fontSize = 12.sp,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = orderId,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

