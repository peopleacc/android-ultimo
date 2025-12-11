package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomCardWithBorder
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*
import kotlinx.coroutines.launch

data class ContactMethod(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val subtitle: String,
    val action: String
)

data class FAQItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpSupportScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: com.ultimo.vehicleapp.ViewModels.SessionViewModel,
    chatViewModel: com.ultimo.vehicleapp.ViewModels.ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val currentUser by sessionViewModel.user.collectAsState()
    var sending by remember { mutableStateOf(false) }
    var sendSuccess by remember { mutableStateOf<Boolean?>(null) }
    var sendError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val contactMethods = listOf(
        ContactMethod(Icons.Default.Phone, "Phone Support", "+62 21 1234 5678", "Mon - Sat, 9:00 AM - 6:00 PM", "tel:+622112345678"),
        ContactMethod(Icons.Default.Email, "Email Support", "support@jokseat.com", "Response within 24 hours", "mailto:support@jokseat.com"),
        ContactMethod(Icons.Default.Chat, "WhatsApp", "+62 812-3456-7890", "Fast response via chat", "https://wa.me/6281234567890")
    )

    val faqItems = listOf(
        FAQItem("Berapa lama waktu pengerjaan?", "Waktu pengerjaan standar adalah 3-4 hari. Kami juga menyediakan layanan express (1-2 hari) dan same day (6-8 jam) dengan biaya tambahan."),
        FAQItem("Apakah garansi tersedia?", "Ya, semua pemasangan jok kami dilengkapi dengan garansi 1 tahun untuk jahitan dan material."),
        FAQItem("Bagaimana cara pembayaran?", "Kami menerima pembayaran melalui QRIS dan cash. Pembayaran dilakukan setelah pengerjaan selesai."),
        FAQItem("Apakah bisa custom desain?", "Ya, kami menerima custom desain sesuai keinginan Anda. Silakan hubungi customer service untuk konsultasi lebih lanjut.")
    )

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
                IconButton(onClick = { onNavigate(Screen.Profile.route) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Help & Support",
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

            // Contact Methods
            Text(
                text = "Contact Us",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            contactMethods.forEach { method ->
                ContactMethodCard(method = method)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // FAQ Section
            Text(
                text = "Frequently Asked Questions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            faqItems.forEach { faq ->
                FAQCard(faq = faq)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Contact Form
            Text(
                text = "Send Us a Message",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            CustomCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Subject",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                    }
                    CustomTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        placeholder = "What can we help you with?",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Message",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryBlue
                        )
                    }
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = {
                            Text(
                                text = "Describe your issue or question...",
                                color = Color(0xFF757575),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BackgroundGray
                        ),
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(
                        text = if (sending) "Sending..." else "Send Message",
                        onClick = {
                            val userId = currentUser?.id
                            if (!sending && userId != null && subject.isNotEmpty() && message.isNotEmpty()) {
                                sending = true
                                coroutineScope.launch {
                                    val result = chatViewModel.sendMessage(userId, subject, message)
                                    sendSuccess = result.ok
                                    sendError = result.error
                                    sending = false
                                    if (result.ok) {
                                        subject = ""
                                        message = ""
                                    }
                                    
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !sending && currentUser?.id != null && subject.isNotEmpty() && message.isNotEmpty(),
                        icon = Icons.Default.Send
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Additional Info
            CustomCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Need immediate assistance?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Our customer service team is available Monday - Saturday, 9:00 AM - 6:00 PM",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            if (sendSuccess != null) {
                val success = sendSuccess == true
                androidx.compose.material3.Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = if (success) SuccessGreen else Color.Red,
                    contentColor = Color.White
                ) {
                    androidx.compose.material3.Text(text = if (success) "Pesan terkirim" else ("Gagal mengirim pesan" + (sendError?.let { ": $it" } ?: "")))
                }
                LaunchedEffect(sendSuccess) {
                    kotlinx.coroutines.delay(2000)
                    sendSuccess = null
                    sendError = null
                }
            }
        }
    }
}

@Composable
fun ContactMethodCard(
    method: ContactMethod
) {
    CustomCardWithBorder(
        onClick = { /* Open contact method */ },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BackgroundGray
            ) {
                Icon(
                    imageVector = method.icon,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = method.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = method.subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun FAQCard(
    faq: FAQItem
) {
    CustomCardWithBorder(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = faq.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = faq.answer,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

