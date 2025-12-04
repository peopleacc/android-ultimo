package com.ultimo.vehicleapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ultimo.vehicleapp.Config.ApiClient
import com.ultimo.vehicleapp.Controller.LoginRequest
import com.ultimo.vehicleapp.Controller.LoginResponse
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(
    sessionViewModel: SessionViewModel,
    onNavigate: (String) -> Unit,
    onLogin: () -> Unit,

    ) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

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
                    // Header
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp) // Tambahkan tinggi biar Box punya ruang
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryBlue, PrimaryBlueLight)
                                )
                            ),
                        contentAlignment = Alignment.Center // ✅ Semua isi Box di tengah
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.slogan),
                                contentDescription = "ULTIMO Logo",
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Let's Get Started!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                color = Color.White
                            )
                        }
                    }


                    // Form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                    ) {
                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Email",
                            modifier = Modifier.fillMaxWidth(),
                            keyboardType = KeyboardType.Email
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
                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔹 Remember Me Checkbox
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryBlue
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remember Me",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔹 Tombol Login
                        CustomButton(
                            text = "Sign In",
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Email dan password tidak boleh kosong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@CustomButton
                                }

                                val request = LoginRequest(email, password)
                                ApiClient.instance.login(request)
                                    .enqueue(object : Callback<LoginResponse> {
                                        override fun onResponse(
                                            call: Call<LoginResponse>,
                                            response: Response<LoginResponse>
                                        ) {
                                            if (response.isSuccessful && response.body()?.status == "success") {

                                                // Ambil token dari response
                                                val token = response.body()?.session ?: ""

                                                // Simpan remember me preference dulu
                                                sessionViewModel.saveRememberMe(rememberMe)
                                                
                                                // Gunakan user data dari LoginResponse jika ada, jika tidak baru fetch dari API
                                                val userData = response.body()?.user
                                                if (userData != null) {
                                                    // Set user data dulu sebelum save token
                                                    sessionViewModel.setUserData(userData)
                                                    // Simpan token setelah user data di-set
                                                    sessionViewModel.saveSessionToken(token)
                                                } else {
                                                    // Jika user data tidak ada di response, fetch dari API
                                                    // Simpan token dulu
                                                    sessionViewModel.saveSessionToken(token)
                                                    // Lalu fetch user data
                                                    sessionViewModel.fetchUserDataAfterLogin(token)
                                                }
                                                
                                                // Tampilkan toast
                                                Toast.makeText(context, "Login berhasil!", Toast.LENGTH_SHORT).show()

                                                // Pindah ke HomeScreen
                                                onLogin()
                                            } else {
                                                // Jika gagal
                                                Toast.makeText(
                                                    context,
                                                    response.body()?.message ?: "Login gagal",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }

                                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                            Toast.makeText(
                                                context,
                                                "Error: ${t.localizedMessage}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Login
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically // ✅ Menjaga sejajar tengah
                        ) {
                            Text(
                                text = "Don't Have Account?",
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            TextButton(onClick = { onNavigate(Screen.Register.route) }) {
                                Text(
                                    text = "Sign Up",
                                    fontSize = 14.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun SocialLoginButton(
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary
        ),
        border = BorderStroke(1.dp, Gray300)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Fit
        )
    }
}
