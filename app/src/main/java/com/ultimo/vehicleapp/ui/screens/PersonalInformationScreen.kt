package com.ultimo.vehicleapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*

@Composable
fun PersonalInformationScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel
) {
    val user by sessionViewModel.user.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val token by sessionViewModel.token.collectAsState()

    LaunchedEffect(Unit) {
        val currentToken = token
        if (user == null && !currentToken.isNullOrEmpty()) {
            sessionViewModel.fetchUserDataAfterLogin(currentToken)
        }
    }


    var name by remember { mutableStateOf(user?.nama ?: "-") }
    var email by remember { mutableStateOf(user?.email ?: "-") }
    var phone by remember { mutableStateOf(user?.phone ?: "-") }
    var address by remember { mutableStateOf(user?.address ?: "-") }
    var dateOfBirth by remember { mutableStateOf(user?.nama ?: "-") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
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
                IconButton(onClick = { onNavigate(Screen.Profile.route) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Personal Information",
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

            // Profile Picture Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryBlue,
                    modifier = Modifier.size(96.dp),
                    border = BorderStroke(4.dp, PrimaryBlue)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = name.split(" ").map { it[0] }.joinToString(""),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryBlue,
                        containerColor = BackgroundGray
                    ),
                    border = BorderStroke(1.dp, Gray300),
                    modifier = Modifier.border(1.dp, Gray300, RoundedCornerShape(12.dp)),
                    enabled = true
                ) {
                    Icon(
                        imageVector = if (profileImageUri != null) Icons.Default.Edit else Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (profileImageUri != null) "Change Photo" else "Change Photo",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Form
            CustomCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    FormField(
                        icon = Icons.Default.Person,
                        label = "Full Name",
                        value = name,
                        onValueChange = { name = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    FormField(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    FormField(
                        icon = Icons.Default.Phone,
                        label = "Phone Number",
                        value = phone,
                        onValueChange = { phone = it },
                        keyboardType = KeyboardType.Phone
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    FormField(
                        icon = Icons.Default.CalendarToday,
                        label = "Date of Birth",
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Address",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryBlue
                            )
                        }
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(96.dp),
                            placeholder = {
                                Text(
                                    text = "Enter your address",
                                    color = Color(0xFF616161),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = BackgroundGray,
                                focusedTextColor = Color(0xFF000000),
                                unfocusedTextColor = Color(0xFF000000),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF000000)
                            ),
                            maxLines = 4
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            CustomButton(
                text = "Save Changes",
                onClick = {
                    onNavigate(Screen.Profile.route)
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Save
            )
        }
    }
}

@Composable
fun FormField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue
            )
        }
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "Enter $label",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = keyboardType
        )
    }
}

