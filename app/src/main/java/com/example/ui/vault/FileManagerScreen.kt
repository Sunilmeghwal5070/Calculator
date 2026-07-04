package com.example.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.navigation.CategoryDetailRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: VaultViewModel,
    onBack: () -> Unit,
    onNavigate: (Any) -> Unit
) {
    val counts by viewModel.categoryCounts.collectAsState()
    val totalFiles = counts.values.sum()

    Scaffold(
        containerColor = Color.Black,
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black
                    ),
                    title = { Text("File manager", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                onClick = { /* Add logic */ },
                containerColor = Color(0xFFFF9F0A),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // All Files Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable { /* Navigate to all files */ },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1C1C1E)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF5E62), Color(0xFFFF9966))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("All files", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("$totalFiles | $totalFiles file", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categories Grid
            val categories = listOf(
                FileCategory("Photos", Icons.Default.Photo, Brush.verticalGradient(listOf(Color(0xFFFF00CC), Color(0xFF333399))), "PHOTO"),
                FileCategory("Videos", Icons.Default.VideoLibrary, Brush.verticalGradient(listOf(Color(0xFFFF9966), Color(0xFFFF5E62))), "VIDEO"),
                FileCategory("Audio", Icons.Default.Audiotrack, Brush.verticalGradient(listOf(Color(0xFFF09819), Color(0xFFEDDE5D))), "AUDIO"),
                FileCategory("Documents", Icons.Default.Description, Brush.verticalGradient(listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))), "DOCUMENT")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    val count = counts[category.type] ?: 0
                    FileCategoryCard(category, count) {
                        onNavigate(CategoryDetailRoute(category.type))
                    }
                }
            }
        }
    }
}

data class FileCategory(val name: String, val icon: ImageVector, val brush: Brush, val type: String)

@Composable
fun FileCategoryCard(category: FileCategory, count: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1C1C1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(category.brush),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            
            Column {
                Text(category.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("$count", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}
