package com.ultimo.vehicleapp.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimo.vehicleapp.Config.ApiClient
import com.ultimo.vehicleapp.Controller.ForgotPasswordRequest
import com.ultimo.vehicleapp.Controller.ForgotPasswordResponse
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ForgotPasswordScreen(
    onNavigate: (String) -> Unit,
    onOTPSent: (String) -> Unit  // Callback dengan email untuk navigasi ke OTP screen
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D336B),
                        Color(0xFF7886C7),
                        Color(0xFFA9B5DF),
                        Color(0xFFE8F9FF),
                        Color(0xFFFFF2F2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PrimaryBlue.copy(alpha = 0.1f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Lupa Password?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Masukkan email Anda untuk menerima kode OTP",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomButton(
                        text = if (isLoading) "Mengirim..." else "Kirim OTP",
                        onClick = {
                            if (email.isBlank()) {
                                Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                return@CustomButton
                            }

                            isLoading = true
                            val request = ForgotPasswordRequest(
                                action = "send_otp",
                                email = email
                            )

                            ApiClient.instance.forgotPassword(request)
                                .enqueue(object : Callback<ForgotPasswordResponse> {
                                    override fun onResponse(
                                        call: Call<ForgotPasswordResponse>,
                                        response: Response<ForgotPasswordResponse>
                                    ) {
                                        isLoading = false
                                        if (response.isSuccessful && response.body()?.status == "success") {
                                            Toast.makeText(context, response.body()?.message ?: "OTP terkirim", Toast.LENGTH_SHORT).show()
                                            onOTPSent(email)
                                        } else {
                                            Toast.makeText(context, response.body()?.message ?: "Gagal mengirim OTP", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                        isLoading = false
                                        Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { onNavigate(Screen.Login.route) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Kembali ke Login",
                            fontSize = 14.sp,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}
