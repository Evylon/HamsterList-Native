package org.stratum0.hamsterlist.android.gui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.models.DialogState

@Composable
fun HamsterListDialog(
    dialogState: DialogState,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onDismiss) {
                Text(dialogState.confirmText)
            }
        },
        text = {
            Text(dialogState.text)
        }
    )
}

@PreviewLightDark
@Composable
fun HamsterListDialogPreview() {
    HamsterListTheme {
        var showDialog by remember { mutableStateOf(true) }
        if (showDialog) {
            HamsterListDialog(
                DialogState.UsernameMissing, { showDialog = false}
            )
        }
    }
}
