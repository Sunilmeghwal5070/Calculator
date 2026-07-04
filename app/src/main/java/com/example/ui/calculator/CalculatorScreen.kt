package com.example.ui.calculator

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel, onUnlock: () -> Unit) {
    val fullExpression by viewModel.fullExpression.collectAsStateWithLifecycle()
    val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val unlockTrigger by viewModel.onUnlock.collectAsStateWithLifecycle()

    var showHistory by remember { mutableStateOf(false) }

    LaunchedEffect(unlockTrigger) {
        if (unlockTrigger) {
            onUnlock()
            viewModel.resetUnlock()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Top Icons (Removed redundant History text button)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .pointerInput(Unit) {
                        var totalDrag = 0f
                        detectVerticalDragGestures(
                            onDragEnd = {
                                if (totalDrag > 150f) { 
                                    showHistory = true
                                } else if (totalDrag < -150f && showHistory) {
                                    showHistory = false
                                }
                                totalDrag = 0f
                            },
                            onVerticalDrag = { _, dragAmount ->
                                totalDrag += dragAmount
                            }
                        )
                    },
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
            val baseFontSize = 64
            val minFontSize = 28
            val threshold = 7
            val fontSizeValue = if (fullExpression.length > threshold) {
                (baseFontSize - (fullExpression.length - threshold) * 4).coerceAtLeast(minFontSize)
            } else {
                baseFontSize
            }
            val fontSize = fontSizeValue.sp

            Text(
                text = fullExpression.ifEmpty { "0" },
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                lineHeight = fontSize * 1.1f
            )
                if (liveResult.isNotEmpty()) {
                    Text(
                        text = liveResult,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            // Divider handle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(3.dp)
                        .background(Color.DarkGray, CircleShape)
                )
            }

            val buttons = listOf(
                listOf("AC", "DEL", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", "00", ".", "=")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { label ->
                        CalculatorButton(
                            label = label,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                when (label) {
                                    "AC" -> viewModel.onClearClick()
                                    "DEL" -> viewModel.onDelClick()
                                    "00" -> viewModel.onNumberClick("00")
                                    "=" -> viewModel.onEqualsClick()
                                    "+", "-", "×", "÷" -> viewModel.onOperatorClick(label)
                                    else -> viewModel.onNumberClick(label)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // History Overlay (unchanged logic but updated colors)
        AnimatedVisibility(
            visible = showHistory,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D0D0D))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.history),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            TextButton(onClick = { viewModel.clearHistory() }) {
                                Text(stringResource(R.string.clear), color = Color(0xFFFF9F0A))
                            }
                            IconButton(onClick = { showHistory = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (history.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_history), color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(history) { item ->
                                Text(
                                    text = item,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                                HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    contentColor: Color? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )

    val defaultBgColor = when (label) {
        "AC", "DEL", "%" -> Color(0xFFA5A5A5)
        "÷", "×", "-", "+", "=" -> Color(0xFFFF9F0A)
        else -> Color(0xFF333333)
    }
    
    val defaultContentColor = when (label) {
        "AC", "DEL", "%" -> Color.Black
        else -> Color.White
    }
    
    val finalBgColor = backgroundColor ?: defaultBgColor
    val finalContentColor = contentColor ?: defaultContentColor
    
    val shadowColor = when (finalBgColor) {
        Color(0xFFA5A5A5) -> Color(0xFF707070)
        Color(0xFFFF9F0A) -> Color(0xFFB86B00)
        else -> Color(0xFF1A1A1A)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shadow layer (3D effect)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isPressed) 1.dp else 4.dp)
                .background(shadowColor, RoundedCornerShape(50))
        )
        
        // Main button layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isPressed) 0.dp else 4.dp)
                .background(finalBgColor, RoundedCornerShape(50))
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (label == "DEL") {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete",
                    tint = finalContentColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = label,
                    color = finalContentColor,
                    fontSize = if (label.length > 1) 22.sp else 28.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
