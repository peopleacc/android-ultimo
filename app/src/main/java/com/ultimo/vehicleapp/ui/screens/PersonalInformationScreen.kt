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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ultimo.vehicleapp.ViewModels.SessionViewModel
import com.ultimo.vehicleapp.Controller.ProfilePhotoRepository
import com.ultimo.vehicleapp.navigation.Screen
import com.ultimo.vehicleapp.ui.components.CustomButton
import com.ultimo.vehicleapp.ui.components.CustomCard
import com.ultimo.vehicleapp.ui.components.CustomTextField
import com.ultimo.vehicleapp.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PersonalInformationScreen(
    onNavigate: (String) -> Unit,
    sessionViewModel: SessionViewModel
) {
    val user by sessionViewModel.user.collectAsState()
    val isLoading by sessionViewModel.isLoading.collectAsState()
    val isUpdating by sessionViewModel.isUpdating.collectAsState()
    val updateMessage by sessionViewModel.updateMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Show update message as snackbar
    LaunchedEffect(updateMessage) {
        val message = updateMessage
        if (!message.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentProfilePhotoUrl by remember { mutableStateOf<String?>(null) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var uploadedPhotoUrl by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Sync state with user data
    LaunchedEffect(user) {
        user?.let { userData ->
            name = userData.nama ?: ""
            email = userData.email ?: ""
            phone = userData.phone ?: ""
            address = userData.address ?: ""
            currentProfilePhotoUrl = userData.foto_profile
        }
    }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        // Upload foto segera setelah dipilih
        uri?.let { selectedUri ->
            val userId = user?.id
            if (userId != null) {
                coroutineScope.launch {
                    isUploadingPhoto = true
                    try {
                        val inputStream = context.contentResolver.openInputStream(selectedUri)
                        val imageBytes = inputStream?.readBytes()
                        inputStream?.close()
                        
                        if (imageBytes != null) {
                            val photoUrl = ProfilePhotoRepository.uploadProfilePhoto(
                                userId = userId,
                                imageBytes = imageBytes,
                                fileExtension = "jpg"
                            )
                            if (photoUrl != null) {
                                uploadedPhotoUrl = photoUrl
                                // Update di database
                                val updateSuccess = ProfilePhotoRepository.updateFotoProfile(userId, photoUrl)
                                if (updateSuccess) {
                                    // ✅ Update user state in SessionViewModel
                                    sessionViewModel.updateUserFotoProfile(photoUrl)
                                    currentProfilePhotoUrl = photoUrl
                                }
                            }
                        }
                    } catch (e: Exception) {
                        println("Error uploading photo: ${e.message}")
                    } finally {
                        isUploadingPhoto = false
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->
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
                // Profile Picture
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Gray200)
                            .border(
                                BorderStroke(3.dp, Color.White),
                                CircleShape
                            )
                            .clickable { if (!isUploadingPhoto) imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isUploadingPhoto -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = PrimaryBlue
                                )
                            }
                            profileImageUri != null -> {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            uploadedPhotoUrl != null -> {
                                AsyncImage(
                                    model = uploadedPhotoUrl,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            currentProfilePhotoUrl != null -> {
                                AsyncImage(
                                    model = currentProfilePhotoUrl,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(60.dp),
                                    tint = TextSecondary
                                )
                            }
                        }
                        
                        // Camera icon overlay
                        if (!isUploadingPhoto) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Personal Information Card
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Basic Information",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        FormField(
                            icon = Icons.Default.Person,
                            label = "Full Name",
                            value = name,
                            onValueChange = { name = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormField(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = email,
                            onValueChange = { email = it },
                            keyboardType = KeyboardType.Email
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormField(
                            icon = Icons.Default.Phone,
                            label = "Phone Number",
                            value = phone,
                            onValueChange = { phone = it },
                            keyboardType = KeyboardType.Phone
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FormField(
                            icon = Icons.Default.Home,
                            label = "Address",
                            value = address,
                            onValueChange = { address = it }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Change Password Link Card
                CustomCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.ChangePassword.route) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BackgroundGray
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Change Password",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Update your account password",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                CustomButton(
                    text = if (isUpdating) "Saving..." else "Save Changes",
                    onClick = {
                        sessionViewModel.updatePersonalInfo(
                            nama = name,
                            email = email,
                            phone = phone,
                            address = address.takeIf { it.isNotEmpty() },
                            currentPassword = null,
                            newPassword = null,
                            onSuccess = {
                                onNavigate(Screen.Profile.route)
                            },
                            onError = { }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Save,
                    enabled = !isUpdating
                )
            }
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
                color = TextPrimary
            )
        }
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "Enter $label",
            keyboardType = keyboardType,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasswordField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
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
                color = TextPrimary
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = Gray200
            )
        )
    }
}
