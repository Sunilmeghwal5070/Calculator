package com.example.ui.vault

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    type: String,
    viewModel: VaultViewModel,
    onBack: () -> Unit,
    onOpenCamera: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val items by viewModel.items.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf("items") } // "items" or "folders"
    var showMenu by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var selectedUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    var showHideDialog by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedUris = uris
            showHideDialog = true
        }
    }

    LaunchedEffect(type) {
        viewModel.loadItems(type)
    }

    Scaffold(
        containerColor = Color(0xFF0D0D0D),
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D0D0D)
                    ),
                    title = { 
                        Text(
                            text = if (selectedTab == "items") type.lowercase().replaceFirstChar { it.uppercase() } else "Folders", 
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        if (selectedTab == "folders") {
                            IconButton(onClick = { showCreateFolderDialog = true }) {
                                Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder", tint = Color.White)
                            }
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1C1C1E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("File loss protection", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color.White) },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Feedback", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Feedback, contentDescription = null, tint = Color.White) },
                                onClick = { showMenu = false }
                            )
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
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .align(Alignment.BottomCenter),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2C2C2E),
                                        Color(0xFF1C1C1E)
                                    )
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = "items" }
                                    .padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = when(type) {
                                        "PHOTO" -> Icons.Default.Photo
                                        "VIDEO" -> Icons.Default.VideoLibrary
                                        "AUDIO" -> Icons.Default.Audiotrack
                                        else -> Icons.Default.Description
                                    },
                                    contentDescription = null,
                                    tint = if (selectedTab == "items") Color(0xFFFF9F0A) else Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = type.lowercase().replaceFirstChar { it.uppercase() },
                                    color = if (selectedTab == "items") Color(0xFFFF9F0A) else Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == "items") FontWeight.Bold else FontWeight.Normal
                                )
                                if (selectedTab == "items") {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(4.dp)
                                            .background(Color(0xFFFF9F0A), CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(80.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = "folders" }
                                    .padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = if (selectedTab == "folders") Color(0xFFFF9F0A) else Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "Folders",
                                    color = if (selectedTab == "folders") Color(0xFFFF9F0A) else Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == "folders") FontWeight.Bold else FontWeight.Normal
                                )
                                if (selectedTab == "folders") {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(4.dp)
                                            .background(Color(0xFFFF9F0A), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { 
                        val mimeType = when(type) {
                            "PHOTO" -> "image/*"
                            "VIDEO" -> "video/*"
                            "AUDIO" -> "audio/*"
                            else -> "*/*"
                        }
                        pickerLauncher.launch(mimeType)
                    },
                    containerColor = Color(0xFFFF9F0A),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(56.dp)
                        .padding(2.dp),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
                }
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(120.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp, 4.dp)
                            .background(Color(0xFFFF9F0A), RoundedCornerShape(2.dp))
                            .offset(y = 10.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Tap \"+\" below to add files to hide",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(item.name, color = Color.White) },
                        supportingContent = { Text("${item.size / 1024} KB", color = Color.Gray) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.unhideItem(item) }) {
                                Icon(Icons.Default.Restore, contentDescription = "Restore", tint = Color(0xFFFF9F0A))
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    if (showHideDialog) {
        HideFilesDialog(
            count = selectedUris.size,
            onDismiss = { showHideDialog = false },
            onConfirm = {
                // Process URIs
                selectedUris.forEach { uri ->
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        viewModel.addItem(file, type, uri, context)
                    }
                }
                showHideDialog = false
                selectedUris = emptyList()
            }
        )
    }

    if (showCreateFolderDialog) {
        CreateFolderDialog(onDismiss = { showCreateFolderDialog = false })
    }
}

fun uriToFile(context: android.content.Context, uri: android.net.Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, UUID.randomUUID().toString())
        val outputStream = FileOutputStream(tempFile)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun HideFilesDialog(count: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1E),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
                Text("Hide selected file(s)", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        },
        text = {
            Text(
                "You've selected $count file(s). Do you want to hide them now?",
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Hide now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Continue to select", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@Composable
fun CreateFolderDialog(onDismiss: () -> Unit) {
    var folderName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1E),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }
                Text("Create new folder", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            OutlinedTextField(
                value = folderName,
                onValueChange = { folderName = it },
                placeholder = { Text("Enter folder name", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF9F0A),
                    unfocusedBorderColor = Color.Gray
                ),
                shape = RoundedCornerShape(24.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("OK", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}
