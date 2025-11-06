package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.viewmodel.LoadingState

@Composable
fun SyncIconButton(
    loadingState: LoadingState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (loadingState) {
        is LoadingState.Done -> R.drawable.ic_sync
        is LoadingState.Error -> R.drawable.ic_sync_problem
        is LoadingState.Loading -> R.drawable.ic_sync_arrow_down
        is LoadingState.SyncEnqueued -> R.drawable.ic_sync_arrow_up
    }
    val color = when (loadingState) {
        is LoadingState.Done -> MaterialTheme.colorScheme.onPrimary
        is LoadingState.Error -> MaterialTheme.colorScheme.error
        is LoadingState.Loading,
        is LoadingState.SyncEnqueued -> HamsterListTheme.colors.warning
    }
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(R.string.navigation_back_button),
            tint = color,
            modifier = Modifier.size(32.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun SyncIconButtonPreview() {
    HamsterListTheme {
        Surface {
            Row {
                listOf(
                    LoadingState.Done,
                    LoadingState.Loading,
                    LoadingState.SyncEnqueued,
                    LoadingState.Error(IllegalStateException(""))
                ).forEach { loadingState ->
                    SyncIconButton(
                        loadingState = loadingState,
                        onClick = {}
                    )
                }
            }
        }
    }
}
