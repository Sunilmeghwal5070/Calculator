package com.example.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import com.example.navigation.*
import com.example.ui.components.PermissionHandler

data class Category(val title: String, val icon: ImageVector, val color: Color, val type: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultHomeScreen(
    viewModel: VaultViewModel,
    onNavigate: (Any) -> Unit
) {
    var permissionsGranted by remember { mutableStateOf(false) }

    if (!permissionsGranted) {
        PermissionHandler(onPermissionsGranted = { permissionsGranted = true })
    }

    val categories = listOf(
        Category("Photos", Icons.Default.Photo, Color(0xFF2196F3), "PHOTO"),
        Category("Videos", Icons.Default.VideoLibrary, Color(0xFFE91E63), "VIDEO"),
        Category("Audio", Icons.Default.Audiotrack, Color(0xFF9C27B0), "AUDIO"),
        Category("Documents", Icons.Default.Description, Color(0xFFFF9800), "DOCUMENT"),
        Category("APK", Icons.Default.Android, Color(0xFF4CAF50), "APK"),
        Category("Notes", Icons.Default.Note, Color(0xFFFFC107), "NOTE"),
        Category("Contacts", Icons.Default.Contacts, Color(0xFF00BCD4), "CONTACT"),
        Category("File Manager", Icons.Default.Folder, Color(0xFF607D8B), "FILE_MANAGER"),
        Category("Apps", Icons.Default.Apps, Color(0xFF673AB7), "APP")
    )

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D0D0D)
                    ),
                    title = { Text("Vault", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                    actions = {
                        IconButton(onClick = { onNavigate(SettingsRoute) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFFFF9F0A).copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add Menu */ },
                containerColor = Color(0xFFFF9F0A),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category = category, onClick = { 
                    when(category.type) {
                        "NOTE" -> onNavigate(NotesRoute)
                        "CONTACT" -> onNavigate(ContactsRoute)
                        "FILE_MANAGER" -> onNavigate(FileManagerRoute)
                        else -> onNavigate(CategoryDetailRoute(category.type))
                    }
                })
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.verticalGradient(
                    colors = listOf(category.color.copy(alpha = 0.8f), category.color.copy(alpha = 0.4f))
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
