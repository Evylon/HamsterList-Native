package org.stratum0.hamsterlist.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Suppress("MagicNumber")
@Composable
fun HamsterListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) hamsterListDarkColors else hamsterListLightColors
    val materialColors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF39823B),
            onPrimary = Color.White,
            secondary = Color(0xFFAE4CAB),
            background = Color.Black
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4CAE4F),
            onPrimary = Color.Black,
            secondary = Color(0xFFAE4CAB),
            background = Color.LightGray
        )
    }
    val typography = Typography(
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    CompositionLocalProvider(LocalHamsterListColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

data class HamsterListColors(
    val shapeBackgroundColor: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified
)

val hamsterListLightColors = HamsterListColors(
    shapeBackgroundColor = Color.Black.copy(alpha = 0.12f),
    warning = Color(0xFFFF9800)
)

val hamsterListDarkColors = HamsterListColors(
    shapeBackgroundColor = Color.White.copy(alpha = 0.12f),
    warning = Color(0xFFFF9800)
)

val LocalHamsterListColors = staticCompositionLocalOf { HamsterListColors() }

object HamsterListTheme {
    val colors: HamsterListColors
        @Composable
        get() = LocalHamsterListColors.current
}
