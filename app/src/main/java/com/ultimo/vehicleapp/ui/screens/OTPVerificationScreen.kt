package com.ultimo.vehicleapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.TextStyle
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
import com.ultimo.vehicleapp.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun OTPVerificationScreen(
    email: String,
    onNavigate: (String) -> Unit,
    onVerified: (String, String) -> Unit  // Callback dengan email dan token untuk navigasi ke Reset screen
) {
    val context = LocalContext.current
    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var resendEnabled by remember { mutableStateOf(true) }

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
                                imageVector = Icons.Default.MarkEmailRead,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Verifikasi OTP",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Masukkan kode 6 digit yang dikirim ke",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = email,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // OTP Input Boxes
                    OTPInputField(
                        otp = otp,
                        onOTPChange = { if (it.length <= 6) otp = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomButton(
                        text = if (isLoading) "Memverifikasi..." else "Verifikasi",
                        onClick = {
                            if (otp.length != 6) {
                                Toast.makeText(context, "Masukkan 6 digit kode OTP", Toast.LENGTH_SHORT).show()
                                return@CustomButton
                            }

                            isLoading = true
                            val request = ForgotPasswordRequest(
                                action = "verify_otp",
                                email = email,
                                otp = otp
                            )

                            ApiClient.instance.forgotPassword(request)
                                .enqueue(object : Callback<ForgotPasswordResponse> {
                                    override fun onResponse(
                                        call: Call<ForgotPasswordResponse>,
                                        response: Response<ForgotPasswordResponse>
                                    ) {
                                        isLoading = false
                                        if (response.isSuccessful && response.body()?.status == "success") {
                                            val token = response.body()?.token ?: ""
                                            Toast.makeText(context, "OTP valid!", Toast.LENGTH_SHORT).show()
                                            onVerified(email, token)
                                        } else {
                                            Toast.makeText(context, response.body()?.message ?: "OTP tidak valid", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                        isLoading = false
                                        Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Check
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Resend OTP
                    TextButton(
                        onClick = {
                            if (!resendEnabled) return@TextButton
                            
                            resendEnabled = false
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
                                        resendEnabled = true
                                        if (response.isSuccessful && response.body()?.status == "success") {
                                            Toast.makeText(context, "OTP baru telah dikirim", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, response.body()?.message ?: "Gagal mengirim ulang", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                                        resendEnabled = true
                                        Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        },
                        enabled = resendEnabled
                    ) {
                        Text(
                            text = "Kirim Ulang OTP",
                            fontSize = 14.sp,
                            color = if (resendEnabled) PrimaryBlue else TextSecondary
                        )
                    }

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

@Composable
fun OTPInputField(
    otp: String,
    onOTPChange: (String) -> Unit
) {
    BasicTextField(
        value = otp,
        onValueChange = onOTPChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""
                    val isFocused = otp.length == index

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(
                                width = 2.dp,
                                color = if (isFocused) PrimaryBlue else Gray300,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = if (char.isNotEmpty()) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}
