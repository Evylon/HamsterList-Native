package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.ShadowGradient(isTop: Boolean = false) {
    Box(
        modifier = Modifier
            .align(
                if (isTop) {
                    Alignment.TopCenter
                } else {
                    Alignment.BottomCenter
                }
            )
            .height(12.dp)
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = if (isTop) {
                        listOf(MaterialTheme.colors.background, Color.Transparent)
                    } else {
                        listOf(Color.Transparent, MaterialTheme.colors.background)
                    }
                )
            )
    )
}
