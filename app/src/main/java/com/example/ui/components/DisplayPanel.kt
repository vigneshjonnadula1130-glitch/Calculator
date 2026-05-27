package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark

@Composable
fun DisplayPanel(
    expression: String,
    displayValue: String,
    liveResult: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Determine font size adaptively to prevent overflow and wrap issues
    val resultFontSize = when {
        displayValue.length <= 6 -> 72.sp
        displayValue.length <= 9 -> 56.sp
        displayValue.length <= 12 -> 42.sp
        else -> 30.sp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // Expression row
            Text(
                text = expression.ifEmpty { " " },
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expression_text")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Box to hold live preview and primary display value
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Live preview (docked to bottom-left of the display row)
                if (liveResult.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                    ) {
                        Text(
                            text = liveResult,
                            color = Color(0xFF2DD4BF).copy(alpha = 0.85f),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.testTag("live_result_text")
                        )
                    }
                }

                // Large Result row (with clean performance-optimized transitions)
                Crossfade(
                    targetState = displayValue,
                    modifier = Modifier.align(Alignment.BottomEnd),
                    label = "DisplayResultTransition"
                ) { targetValue ->
                    Text(
                        text = targetValue,
                        color = Color.White,
                        fontSize = resultFontSize,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier
                            .testTag("display_result_text")
                    )
                }
            }
        }

        // Copy icon in the corner of display area
        IconButton(
            onClick = {
                try {
                    val valueToCopy = if (liveResult.isNotEmpty()) {
                        liveResult.replace("= ", "").trim()
                    } else {
                        displayValue
                    }
                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    if (clipboardManager != null) {
                        val clip = ClipData.newPlainText("Calculator Result", valueToCopy)
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard: $valueToCopy", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Clipboard not available", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .testTag("copy_result_button")
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy result",
                tint = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
