package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.gui.utils.toColor
import org.stratum0.hamsterlist.viewmodel.shoppinglist.CategoryCircleState

@Composable
fun CategoryCircle(
    uiState: CategoryCircleState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .size(40.dp)
            .drawBehind {
                drawCircle(color = uiState.categoryColor.toColor())
            }
    ) {
        HamsterListTheme(darkTheme = uiState.categoryTextLight) {
            Text(
                text = uiState.category,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
