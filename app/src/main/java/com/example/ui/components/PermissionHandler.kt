package com.example.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionsGranted: () -> Unit
) {
    val permissions = mutableListOf(
        Manifest.permission.CAMERA,
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
            add(Manifest.permission.READ_MEDIA_VIDEO)
            add(Manifest.permission.READ_MEDIA_AUDIO)
            add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    if (permissionState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
    } else {
        AlertDialog(
            onDismissRequest = { /* Don't allow dismiss */ },
            title = {
                Text(
                    text = "Permissions Required",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "To protect your photos, videos, and files, Calculator Vault needs the following permissions:",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    permissions.forEach { permission ->
                        val label = when (permission) {
                            Manifest.permission.CAMERA -> "• Camera (to take private photos)"
                            Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE -> "• Media Access (to import photos/videos)"
                            Manifest.permission.POST_NOTIFICATIONS -> "• Notifications (for status updates)"
                            else -> null
                        }
                        label?.let {
                            Text(text = it, color = Color.White, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { permissionState.launchMultiplePermissionRequest() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9F0A))
                ) {
                    Text("Grant Access", color = Color.White)
                }
            },
            containerColor = Color(0xFF1B1B1B)
        )
    }
}
