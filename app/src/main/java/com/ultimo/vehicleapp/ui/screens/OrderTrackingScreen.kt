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
import com.ultimo.vehicleapp.navigation.Screen
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
    onNavigate: (String) -> Unit
) {
    val orders = listOf(
        Order(
            id = "ORD-001",
            service = "Premium Leather Installation",
            status = "In Progress",
            progress = 65,
            currentStep = "Installation",
            estimatedTime = "2 hours",
            startDate = "Nov 2, 2025 09:00 AM",
            estimatedCompletion = "Nov 2, 2025 03:00 PM",
            timeline = listOf(
                TimelineItem(1, "Order Confirmed", "Your order has been confirmed", "09:00 AM", true, icon = Icons.Default.CheckCircle),
                TimelineItem(2, "Material Preparation", "Preparing materials for installation", "10:00 AM", true, icon = Icons.Default.Inventory),
                TimelineItem(3, "Installation In Progress", "Installing your new seat covers", "11:30 AM", false, true, icon = Icons.Default.Build),
                TimelineItem(4, "Quality Check", "Final inspection and quality assurance", "02:00 PM", false, icon = Icons.Default.Star),
                TimelineItem(5, "Ready for Pickup", "Your vehicle is ready", "03:00 PM", false, icon = Icons.Default.CheckCircle)
            )
        ),
        Order(
            id = "ORD-002",
            service = "Sports Racing Design",
            status = "In Progress",
            progress = 45,
            currentStep = "Material Preparation",
            estimatedTime = "4 hours",
            startDate = "Nov 3, 2025 10:00 AM",
            estimatedCompletion = "Nov 3, 2025 04:00 PM",
            timeline = listOf(
                TimelineItem(1, "Order Confirmed", "Your order has been confirmed", "10:00 AM", true, icon = Icons.Default.CheckCircle),
                TimelineItem(2, "Material Preparation", "Preparing materials for installation", "11:00 AM", false, true, icon = Icons.Default.Inventory),
                TimelineItem(3, "Installation In Progress", "Installing your new seat covers", "01:00 PM", false, icon = Icons.Default.Build),
                TimelineItem(4, "Quality Check", "Final inspection and quality assurance", "03:00 PM", false, icon = Icons.Default.Star),
                TimelineItem(5, "Ready for Pickup", "Your vehicle is ready", "04:00 PM", false, icon = Icons.Default.CheckCircle)
            )
        )
    )

    var selectedOrderId by remember { mutableStateOf(orders[0].id) }
    var showOrderSelector by remember { mutableStateOf(false) }

    val selectedOrder = orders.find { it.id == selectedOrderId } ?: orders[0]

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

                // Order Selector
                CustomButton(
                    text = "Pilih Pesanan (${orders.size} pesanan aktif)",
                    onClick = { showOrderSelector = !showOrderSelector },
                    modifier = Modifier.fillMaxWidth(),
                    icon = if (showOrderSelector) Icons.Default.ExpandLess else Icons.Default.ExpandMore
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (showOrderSelector) {
                        orders.forEach { order ->
                            CustomCardWithBorder(
                                onClick = {
                                    selectedOrderId = order.id
                                    showOrderSelector = false
                                },
                                borderColor = if (selectedOrderId == order.id) PrimaryBlue else BackgroundGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = order.id,
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = order.service,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when {
                                            order.progress >= 80 -> SuccessGreen.copy(alpha = 0.2f)
                                            order.progress >= 50 -> InfoBlue.copy(alpha = 0.2f)
                                            else -> WarningOrange.copy(alpha = 0.2f)
                                        }
                                    ) {
                                        Text(
                                            text = "${order.progress}%",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                order.progress >= 80 -> SuccessGreen
                                                order.progress >= 50 -> InfoBlue
                                                else -> WarningOrange
                                            }
                                        )
                                    }
                                }
                            }
                        }
                }

                // Order Info Card
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

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

