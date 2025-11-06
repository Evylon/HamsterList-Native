package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    modifier: Modifier = Modifier
) {
    Text(
        text = throwable.message ?: "Unknown Error",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.fillMaxWidth(),
    )
}

@PreviewLightDark
@Composable
fun ShoppingListPagePreview() {
    HamsterListTheme {
        Surface {
            ErrorContent(
                throwable = IOException("A problem with the network connection occurred."),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
