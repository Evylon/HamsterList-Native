package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.animation.core.FastOutLinearInEasing
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
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R

@Composable
fun HamsterListLoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteSpec = rememberInfiniteTransition()
    val repeatSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            500,
            easing = FastOutLinearInEasing
        ),
        repeatMode = RepeatMode.Reverse
    )
    val bounce = infiniteSpec.animateFloat(
        0f,
        200f,
        animationSpec = repeatSpec,
        label = "Loading Bounce"
    )
    val stretchX = infiniteSpec.animateFloat(
        0.4f,
        0.45f,
        animationSpec = repeatSpec,
        label = "Loading StretchX"
    )
    val stretchY = infiniteSpec.animateFloat(
        0.4f,
        0.35f,
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
