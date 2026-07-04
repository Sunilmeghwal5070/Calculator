package com.example.ui.setup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.calculator.CalculatorButton
import com.example.ui.components.PermissionHandler

@Composable
fun SetupScreen(viewModel: SetupViewModel, onComplete: () -> Unit) {
    val step by viewModel.step.collectAsStateWithLifecycle()
    val pin by viewModel.pin.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        when (step) {
            0 -> WelcomeStep(onStart = { viewModel.nextStep() })
            1 -> PermissionStep(onAllow = { viewModel.nextStep() })
            2 -> PinSetupStep(
                title = stringResource(R.string.pin_setup_title),
                pin = pin,
                error = error,
                onNumberClick = viewModel::onNumberClick,
                onDelClick = viewModel::onDelClick,
                onClearClick = viewModel::onClearPin,
                onEqualsClick = viewModel::onPinSubmit
            )
            3 -> PinSetupStep(
                title = stringResource(R.string.pin_confirm_title),
                pin = pin,
                error = error,
                onNumberClick = viewModel::onNumberClick,
                onDelClick = viewModel::onDelClick,
                onClearClick = viewModel::onClearPin,
                onEqualsClick = viewModel::onPinSubmit
            )
            4 -> RecoveryMethodStep(
                onMethodSelected = { method ->
                    if (method == "Security question") {
                        viewModel.nextStep()
                    }
                }
            )
            5 -> SecurityQuestionStep(
                onComplete = { q, a ->
                    viewModel.onCompleteSetup(q, a)
                    onComplete()
                }
            )
        }
    }
}

@Composable
fun WelcomeStep(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color(0xFF1A1A1A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFFF9F0A),
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Hidden behind a calculator",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Securely hide your photos, videos, and apps — all invisible to others.",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(stringResource(R.string.start), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun PermissionStep(onAllow: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFFFF9F0A),
                modifier = Modifier.size(80.dp)
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Unlock full protection",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "All files access is required to hide files on your device. Please grant Calculator this permission to ensure the normal use of the app.",
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onAllow,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(stringResource(R.string.allow_access), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun PinSetupStep(
    title: String,
    pin: String,
    error: String?,
    onNumberClick: (String) -> Unit,
    onDelClick: () -> Unit,
    onClearClick: () -> Unit,
    onEqualsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // PIN Display area (Calculator style)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            val displayPin = if (pin.isEmpty()) "0" else "*".repeat(pin.length)
            Text(
                text = displayPin,
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }

        // Divider handle (Like in main calculator)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(Color.DarkGray, CircleShape)
            )
        }

        val buttonRows = listOf(
            listOf("AC", "DEL", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", "00", ".", "=")
        )

        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        label = label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (label) {
                                "=" -> onEqualsClick()
                                "AC" -> onClearClick()
                                "DEL" -> onDelClick()
                                "00" -> onNumberClick("00")
                                "%", "÷", "×", "-", "+" -> { /* Do nothing for operator in PIN setup */ }
                                else -> onNumberClick(label)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RecoveryMethodStep(onMethodSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        val annotatedTitle = buildAnnotatedString {
            append("Set your ")
            withStyle(style = SpanStyle(color = Color(0xFFFF9F0A))) {
                append("password recovery")
            }
            append(" method")
        }
        Text(
            text = annotatedTitle,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        RecoveryMethodCard(
            title = "Security question",
            icon = Icons.Outlined.Security,
            onClick = { onMethodSelected("Security question") }
        )
    }
}

@Composable
fun RecoveryMethodCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFF9F0A),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun SecurityQuestionStep(onComplete: (String, String) -> Unit) {
    var question by remember { mutableStateOf("What is your birth city?") }
    var answer by remember { mutableStateOf("") }
    val questions = listOf(
        "What is your birth city?",
        "What is your mother's maiden name?",
        "What was the name of your first pet?",
        "What is your favorite book?",
        "What is your father's middle name?"
    )
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = stringResource(R.string.security_question),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Used to reset your password if forgotten.",
            color = Color.Gray,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = question,
                onValueChange = {},
                readOnly = true,
                label = { Text("Security Question", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF9F0A),
                    unfocusedBorderColor = Color.DarkGray
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.clickable { expanded = true }
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.85f).background(Color(0xFF1A1A1A))
            ) {
                questions.forEach { q ->
                    DropdownMenuItem(
                        text = { Text(q, color = Color.White) },
                        onClick = {
                            question = q
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text(stringResource(R.string.your_answer), color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFFF9F0A),
                unfocusedBorderColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { if (answer.isNotBlank()) onComplete(question, answer) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(28.dp),
            enabled = answer.isNotBlank()
        ) {
            Text(stringResource(R.string.save), color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
