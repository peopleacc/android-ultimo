package com.ultimo.vehicleapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.ultimo.vehicleapp.Config.ApiClient
import com.ultimo.vehicleapp.Controller.LoginResponse
import com.ultimo.vehicleapp.Controller.RegisterRequest
import com.ultimo.vehicleapp.Controller.RegisterResponse
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(
    onNavigate: (String) -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, PrimaryBlue, RoundedCornerShape(40.dp)),
                shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    // Top Section with Wave
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, PrimaryBlueLight)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Back Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(Screen.Login.route) },
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Back",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Logo
                            Image(
                                painter = painterResource(id = R.drawable.slogan),
                                contentDescription = "",
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Fit
                            )

                            Text(
                                text = "",
                                fontSize = 2.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Form Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundPink)
                            .padding(top = 16.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            placeholder = "Full Name",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Email",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Email
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = "Phone Number",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Phone
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = "Password",
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true,
                            showPassword = showPassword,
                            onPasswordToggle = { showPassword = !showPassword }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = agreeTerms,
                                onCheckedChange = { agreeTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryBlue,
                                    uncheckedColor = PrimaryBlue
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Agree with Terms & Policy.",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        CustomButton(
                            text = "Sign Up",
                            onClick = {
                                if (email.isBlank() || password.isBlank() || phone.isBlank() || fullName.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Semua field wajib diisi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@CustomButton
                                }

                                    // ✅ phone tidak perlu .toInt() karena di RegisterRequest sudah String
                                val request = RegisterRequest(fullName, email, phone, password)

                                ApiClient.instance.register(request)
                                    .enqueue(object : Callback<RegisterResponse> {
                                        override fun onResponse(
                                            call: Call<RegisterResponse>,
                                            response: Response<RegisterResponse>
                                        ) {
                                            if (response.isSuccessful && response.body()?.status == "success") {
                                                Toast.makeText(
                                                    context,
                                                    "Registrasi berhasil!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onRegister() // ✅ navigasi ke login atau home sesuai alur kamu
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    response.body()?.message ?: "Registrasi gagal",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                            Toast.makeText(
                                                context,
                                                "Error: ${t.localizedMessage}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already have an account? ",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Login",
                                fontSize = 14.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    onNavigate(Screen.Login.route)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))


                        }
                    }
                }
            }
        }
    }



