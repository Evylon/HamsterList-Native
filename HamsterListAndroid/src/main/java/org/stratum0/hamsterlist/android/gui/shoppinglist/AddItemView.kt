package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R

@Composable
fun AddItemView(
    addItemInput: String,
    addItem: (itemInput: String) -> Unit,
    onItemInputChange: (input: String) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val addItemWithCompletion = {
        if (addItemInput.isNotBlank()) {
            addItem(addItemInput)
            onItemInputChange("")
            focusManager.clearFocus()
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = addItemInput,
            placeholder = {
                Text(stringResource(R.string.hamsterList_newItem_placeholder))
            },
            onValueChange = onItemInputChange,
            keyboardActions = KeyboardActions(onDone = {
                addItemWithCompletion()
            }),
            enabled = isEnabled,
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                IconButton(
                    enabled = isEnabled,
                    onClick = {
                        // TODO display category suggestion and allow user to choose category
                        addItemWithCompletion()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.hamsterList_newItem_icon),
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        )
    }
}

private class AddItemInputParameters : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        "",
        "Short input",
        "Very long input but line breaks are for losers",
        buildString {
            appendLine("Sorry about the last one.")
            appendLine("Line breaks are really useful.")
            appendLine("Also did you notice that the")
            appendLine("last preview is missing a dot at")
            append("the end of the sentence.")
        },
    )
}

@PreviewLightDark
@Composable
fun AddItemViewPreview(
    @PreviewParameter(AddItemInputParameters::class) input: String
) {
    HamsterListTheme {
        Surface {
            AddItemView(
                addItemInput = input,
                addItem = {},
                onItemInputChange = {},
                isEnabled = true,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
