package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R

private const val ANIMATION_DURATION = 500
private const val BOUNCE_INITIAL = 0f
private const val BOUNCE_FULL = 200f
private const val STRETCH_X_INITIAL = 0.4f
private const val STRETCH_X_FULL = 0.45f
private const val STRETCH_Y_INITIAL = 0.4f
private const val STRETCH_Y_FULL = 0.35f

@Suppress("MagicNumber")
private val easing = CubicBezierEasing(0.5f, 0.0f, 0.75f, 0.4f)

@Composable
fun HamsterListLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteSpec = rememberInfiniteTransition()
    val repeatSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = ANIMATION_DURATION,
            easing = easing
        ),
        repeatMode = RepeatMode.Reverse
    )
    val bounce = infiniteSpec.animateFloat(
        initialValue = BOUNCE_INITIAL,
        targetValue = BOUNCE_FULL,
        animationSpec = repeatSpec,
        label = "Loading Bounce"
    )
    val stretchX = infiniteSpec.animateFloat(
        initialValue = STRETCH_X_INITIAL,
        targetValue = STRETCH_X_FULL,
        animationSpec = repeatSpec,
        label = "Loading StretchX"
    )
    val stretchY = infiniteSpec.animateFloat(
        initialValue = STRETCH_Y_INITIAL,
        targetValue = STRETCH_Y_FULL,
        animationSpec = repeatSpec,
        label = "Loading StretchY"
    )
    Image(
        painter = painterResource(R.drawable.hamster),
        contentDescription = stringResource(R.string.hamsterList_logo_description),
        modifier = modifier
            .graphicsLayer(scaleX = stretchX.value, scaleY = stretchY.value)
            .offset(y = bounce.value.dp)
    )
}

@PreviewLightDark
@Composable
private fun HamsterListLoadingIndicatorPreview() {
    HamsterListTheme {
        Surface(Modifier.fillMaxSize()) {
            HamsterListLoadingIndicator(
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
