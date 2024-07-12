package de.evylon.shoppinglist.android.gui.utils

import androidx.compose.ui.graphics.Color
import de.evylon.shoppinglist.models.CSSColor

fun CSSColor.toColor() = when (this) {
    is CSSColor.RGBAColor -> Color(red, green, blue, alpha)
    is CSSColor.HSLColor -> Color.hsl(hue.toFloat(), saturation.toFloat(), lightness.toFloat())
}
