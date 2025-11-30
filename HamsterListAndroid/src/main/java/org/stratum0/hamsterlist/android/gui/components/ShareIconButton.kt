package org.stratum0.hamsterlist.android.gui.components

import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListAction

@Composable
fun ShareIconButton(
    onAction: (ShoppingListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val openShareSheet: (String) -> Unit = { url ->
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }.let {
            Intent.createChooser(it, "Share HamsterList")
        }
        context.startActivity(intent)
    }
    IconButton(
        onClick = {
            onAction(ShoppingListAction.ShareList(openShareSheet))
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = stringResource(R.string.hamsterList_shareButton_description),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(28.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun ShareIconButtonPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            ShareIconButton({})
        }
    }
}
