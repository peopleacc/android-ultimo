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
fun ResetPasswordScreen(
    email: String,
    token: String,
    onNavigate: (String) -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
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
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Reset Password",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Buat password baru untuk akun Anda",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CustomTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = "Password Baru",
                        modifier = Modifier.fillMaxWidth(),
                        isPassword = true,
                        showPassword = showPassword,
                        onPasswordToggle = { showPassword = !showPassword }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Konfirmasi Password",
                        modifier = Modifier.fillMaxWidth(),
                        isPassword = true,
                        showPassword = showConfirmPassword,
                        onPasswordToggle = { showConfirmPassword = !showConfirmPassword }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password requirements
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PasswordRequirement(
                            text = "Minimal 6 karakter",
                            isMet = newPassword.length >= 6
                        )
                        PasswordRequirement(
                            text = "Password cocok",
                            isMet = newPassword.isNotEmpty() && newPassword == confirmPassword
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CustomButton(
                        text = if (isLoading) "Menyimpan..." else "Reset Password",
                        onClick = {
                            if (newPassword.length < 6) {
                                Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                                return@CustomButton
                            }
                            if (newPassword != confirmPassword) {
                                Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                                return@CustomButton
                            }

                            isLoading = true
                            val request = ForgotPasswordRequest(
                                action = "reset_password",
                                email = email,
                                token = token,
                                new_password = newPassword
                            )

                            ApiClient.instance.forgotPassword(request)
                                .enqueue(object : Callback<ForgotPasswordResponse> {
                                    override fun onResponse(
                                        call: Call<ForgotPasswordResponse>,
                                        response: Response<ForgotPasswordResponse>
                                    ) {
                                        isLoading = false
                                        if (response.isSuccessful && response.body()?.status == "success") {
                                            Toast.makeText(context, "Password berhasil direset!", Toast.LENGTH_SHORT).show()
                                            onSuccess()
                                        } else {
                                            Toast.makeText(context, response.body()?.message ?: "Gagal reset password", Toast.LENGTH_SHORT).show()
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
fun PasswordRequirement(
    text: String,
    isMet: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isMet) Color(0xFF4CAF50) else TextSecondary
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isMet) Color(0xFF4CAF50) else TextSecondary
        )
    }
}
