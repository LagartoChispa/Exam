package com.exam.me.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Define el esquema de colores para el tema oscuro de la aplicación CinePlus.
 * Estos colores se utilizan en toda la aplicación para mantener un aspecto coherente.
 */
private val CinePlusDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
)

/**
 * El tema principal de la aplicación.
 *
 * @param content El contenido Composable que se mostrará dentro de este tema.
 */
@Composable
fun ExamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CinePlusDarkColorScheme, // Aplica el esquema de colores personalizado.
        typography = Typography, // Aplica los estilos de tipografía personalizados.
        content = content
    )
}