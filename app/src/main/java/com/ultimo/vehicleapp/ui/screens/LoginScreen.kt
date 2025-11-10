package com.ultimo.vehicleapp.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.font.FontStyle
import com.ultimo.vehicleapp.R
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*

@Composable
fun LoginScreen(
    onNavigate: (String) -> Unit,
    onLogin: () -> Unit
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
                            // Logo
                            Image(
                                painter = painterResource(id = R.drawable.slogan),
                                contentDescription = "ULTIMO Logo",
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Let's Get Started!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                color = Color.White
                            )
                        }
                    }

                    // Form Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp)
                    ) {
                        CustomTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "Username",
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
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryBlue
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Keep Signed",
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        CustomButton(
                            text = "Sign In",
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    onLogin()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Login
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(modifier = Modifier.weight(1f))
                            Text(
                                text = "OR",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Divider(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SocialLoginButton(
                                iconResId = R.drawable.chrome,
                                onClick = { 
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://share.google/Q3W7uuTBHpzcKNWBc"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            SocialLoginButton(
                                iconResId = R.drawable.instagram,
                                onClick = { 
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ultimatemodified?igsh=MzZyOTJ0anZ5dnVn"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Don't Have Account? ",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
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
