package br.com.heitor.tarefas.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val scheme = lightColorScheme(
    primary = Color(0xFF245A44),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EFDC),
    onPrimaryContainer = Color(0xFF123D2C),
    secondary = Color(0xFF53695D),
    background = Color(0xFFF8FAF7),
    surface = Color.White,
    onSurface = Color(0xFF1B2B22),
    surfaceVariant = Color(0xFFE8EEE8)
)

@Composable
fun TarefasTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
