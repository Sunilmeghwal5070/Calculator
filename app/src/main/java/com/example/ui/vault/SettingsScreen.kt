package com.example.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.ui.setup.SetupViewModel
import com.example.navigation.SetupRoute
import com.example.navigation.ChangeIconRoute
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    setupViewModel: SetupViewModel,
    onBack: () -> Unit,
    onNavigate: (Any) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D)
                ),
                title = { 
                    Text(
                        stringResource(R.string.settings), 
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingsSection("Advanced") {
                SettingsItem(
                    title = stringResource(R.string.change_app_icon),
                    icon = Icons.Default.Calculate,
                    onClick = { onNavigate(ChangeIconRoute) },
                    trailing = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFFF9F0A), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        }
                    }
                )
                
                val preventScreenshots by viewModel.preventScreenshots.collectAsState()
                var showScreenshotWarning by remember { mutableStateOf(false) }

                SettingsItem(
                    title = stringResource(R.string.prevent_screenshots),
                    subtitle = if (preventScreenshots) "Screen will be BLACK in AI Studio preview (Working as intended)" else stringResource(R.string.prevent_screenshots_desc),
                    icon = Icons.Default.Screenshot,
                    trailing = {
                        Switch(
                            checked = preventScreenshots,
                            onCheckedChange = { 
                                if (it) {
                                    showScreenshotWarning = true
                                } else {
                                    viewModel.togglePreventScreenshots(false)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFFF9F0A),
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color(0xFF333333)
                            )
                        )
                    }
                )

                if (showScreenshotWarning) {
                    AlertDialog(
                        onDismissRequest = { showScreenshotWarning = false },
                        containerColor = Color(0xFF1C1C1E),
                        title = { Text("Enable Screenshot Protection?", color = Color.White) },
                        text = {
                            Text(
                                "Enabling this will make the screen completely BLACK in the AI Studio preview window for security. " +
                                "This is NOT an error, but a sign that the protection is working. " +
                                "On a real device, it prevents other apps from taking screenshots of your private data.",
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.togglePreventScreenshots(true)
                                showScreenshotWarning = false
                            }) {
                                Text("Enable", color = Color(0xFFFF9F0A))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showScreenshotWarning = false }) {
                                Text("Cancel", color = Color.White)
                            }
                        }
                    )
                }
            }

            SettingsSection("Security") {
                SettingsItem(
                    title = "Change password",
                    icon = Icons.Default.Key,
                    onClick = { 
                        setupViewModel.setStep(2)
                        onNavigate(SetupRoute) 
                    }
                )
                SettingsItem(
                    title = "Password recovery",
                    icon = Icons.Default.Security,
                    trailing = {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                    },
                    onClick = { 
                        setupViewModel.setStep(4)
                        onNavigate(SetupRoute) 
                    }
                )
            }

            SettingsSection("General") {
                val currentLanguage by viewModel.language.collectAsState()
                var showLanguageDialog by remember { mutableStateOf(false) }

                SettingsItem(
                    title = stringResource(R.string.language),
                    subtitle = currentLanguage,
                    icon = Icons.Default.Language,
                    onClick = { showLanguageDialog = true },
                    trailing = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    }
                )

                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        containerColor = Color(0xFF1C1C1E),
                        title = { Text(stringResource(R.string.language), color = Color.White) },
                        text = {
                            Column {
                                listOf("English", "Hindi").forEach { lang ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setLanguage(lang)
                                                showLanguageDialog = false
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = currentLanguage == lang,
                                            onClick = null,
                                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFF9F0A))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(lang, color = Color.White)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text("Close", color = Color(0xFFFF9F0A))
                            }
                        }
                    )
                }

                SettingsItem(
                    title = "Share app",
                    icon = Icons.Default.Share,
                    onClick = { viewModel.shareApp() }
                )
            }

            SettingsSection("Help center") {
                SettingsItem(title = "Rate", icon = Icons.Default.ThumbUp)
                SettingsItem(title = "FAQ", icon = Icons.AutoMirrored.Filled.HelpOutline)
                SettingsItem(title = "Feedback", icon = Icons.AutoMirrored.Filled.Chat)
                SettingsItem(title = "Privacy policy", icon = Icons.Default.Info)
                SettingsItem(title = "Terms of use", icon = Icons.Default.Description)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Version: 1.1.1.2",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Developer by Sunil meghwal",
                color = Color(0xFFFF9F0A).copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = Color(0xFFFF9F0A), fontSize = 12.sp)
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}
