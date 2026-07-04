package com.example.ui.vault

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

data class IconOption(
    val name: String,
    val alias: String,
    val iconRes: Int,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeIconScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val currentAlias by viewModel.currentAlias.collectAsState()
    var selectedAlias by remember { mutableStateOf(currentAlias) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val options = listOf(
        IconOption("Calculator", "com.example.CalculatorAlias", R.drawable.ic_launcher_foreground, "Calculator"),
        IconOption("Weather", "com.example.WeatherAlias", R.drawable.ic_disguise_weather_foreground, "Weather"),
        IconOption("Clock", "com.example.ClockAlias", R.drawable.ic_disguise_clock_foreground, "Clock"),
        IconOption("Notes", "com.example.NoteAlias", R.drawable.ic_disguise_note_foreground, "Note"),
        IconOption("Browser", "com.example.BrowserAlias", R.drawable.ic_disguise_browser_foreground, "Browser"),
        IconOption("Camera", "com.example.CameraAlias", R.drawable.ic_disguise_camera_foreground, "Camera"),
        IconOption("Music", "com.example.MusicAlias", R.drawable.ic_disguise_music_foreground, "Music"),
        IconOption("Gallery", "com.example.GalleryAlias", R.drawable.ic_disguise_gallery_foreground, "Gallery"),
        IconOption("Files", "com.example.FilesAlias", R.drawable.ic_disguise_files_foreground, "Files"),
        IconOption("Settings", "com.example.SettingsAlias", R.drawable.ic_disguise_settings_foreground, "Settings")
    )

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0D)
                ),
                title = { Text("Change app icon", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp),
                enabled = selectedAlias != currentAlias
            ) {
                Text("Change", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text("Current icon", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 16.dp))
            
            val currentOption = options.find { it.alias == currentAlias } ?: options.first()
            IconItem(option = currentOption, isSelected = true, onClick = {})

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            options.groupBy { it.category }.forEach { (category, categoryOptions) ->
                Text(category, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
                
                // Using a simple Row for each category since LazyVerticalGrid inside Scrollable Column is complex
                // Better to just chunk the list and show rows
                categoryOptions.chunked(4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { option ->
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                IconGridItem(
                                    option = option,
                                    isSelected = selectedAlias == option.alias,
                                    onClick = { selectedAlias = option.alias }
                                )
                            }
                        }
                        // Add empty spacers if row is not full
                        repeat(4 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for bottom bar
        }
    }

    if (showConfirmDialog) {
        val selectedOption = options.find { it.alias == selectedAlias }!!
        val currentOption = options.find { it.alias == currentAlias }!!
        
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color(0xFF1C1C1E),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconPreview(currentOption.iconRes, currentOption.name)
                    Icon(
                        imageVector = Icons.Default.CheckCircle, // Using check circle as a swap indicator
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp).size(24.dp)
                    )
                    IconPreview(selectedOption.iconRes, selectedOption.name)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Tap \"Set\" to apply the new icon.",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Next time, open the app by tapping the new icon.",
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateIcon(selectedAlias)
                        showConfirmDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Set", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun IconItem(option: IconOption, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFFFF9F0A) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = option.iconRes),
            contentDescription = null,
            modifier = Modifier.size(56.dp)
        )
    }
}

@Composable
fun IconGridItem(option: IconOption, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C1C1E))
                .clickable(onClick = onClick)
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color(0xFFFF9F0A) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = option.iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                        .background(Color(0xFFFF9F0A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
fun IconPreview(iconRes: Int, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, color = Color.Gray, fontSize = 12.sp)
    }
}
