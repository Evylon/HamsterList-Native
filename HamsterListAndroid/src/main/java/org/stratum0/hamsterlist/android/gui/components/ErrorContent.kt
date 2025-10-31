package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import java.io.IOException

@Composable
fun ErrorContent(
    throwable: Throwable,
    refresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(60.dp)
        )
        Text(
            text = throwable.message ?: "Unkown Error",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
        Button(refresh) {
            Text("Retry")
        }
    }
}

@PreviewLightDark
@Composable
fun ShoppingListPagePreview() {
    HamsterListTheme {
        Surface {
            ErrorContent(
                throwable = IOException("A problem with the network connection occured"),
                refresh = {}
            )
        }
    }
}
