package com.visa.checklist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Forest = Color(0xFF0B3D2E)
private val Leaf = Color(0xFF1F6F54)
private val Cream = Color(0xFFF3F7F5)
private val Ink = Color(0xFF12201A)

private val colors = lightColorScheme(
    primary = Forest,
    onPrimary = Cream,
    secondary = Leaf,
    onSecondary = Cream,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink
)

@Composable
fun VisaChecklistTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
