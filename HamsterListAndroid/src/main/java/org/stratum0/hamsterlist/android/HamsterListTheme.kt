package org.stratum0.hamsterlist.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
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
        darkColors(
            primary = Color(0xFF4CAE4F),
            primaryVariant = Color(0xFF7AAE4C),
            secondary = Color(0xFFAE4CAB),
            background = Color.Black
        )
    } else {
        lightColors(
            primary = Color(0xFF4CAE4F),
            primaryVariant = Color(0xFF7AAE4C),
            secondary = Color(0xFFAE4CAB),
            background = Color.LightGray
        )
    }
    val typography = Typography(
        body1 = TextStyle(
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
            colors = materialColors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

data class HamsterListColors(
    val shapeBackgroundColor: Color = Color.Unspecified
)

val hamsterListLightColors = HamsterListColors(
    shapeBackgroundColor = Color.Black.copy(alpha = 0.12f)
)

val hamsterListDarkColors = HamsterListColors(
    shapeBackgroundColor = Color.White.copy(alpha = 0.12f)
)

val LocalHamsterListColors = staticCompositionLocalOf { HamsterListColors() }

object HamsterListTheme {
    val colors: HamsterListColors
        @Composable
        get() = LocalHamsterListColors.current
}
