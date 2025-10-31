package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState

@Suppress("LongParameterList")
@Composable
fun ShoppingListItem(
    itemState: ItemState,
    isEnabled: Boolean,
    deleteItem: (Item) -> Unit,
    changeItem: (itemText: String) -> Unit,
    showCategoryChooser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            CategoryCircle(
                uiState = itemState.categoryCircleState,
                modifier = Modifier.clickable {
                    showCategoryChooser()
                }
            )
            ItemTextField(
                itemText = itemState.item.toString(),
                isEnabled = isEnabled,
                changeItem = changeItem,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                enabled = isEnabled,
                onClick = { deleteItem(itemState.item) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.hamsterList_deleteItem_icon),
                    tint = MaterialTheme.colors.error
                )
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun ItemTextField(
    itemText: String,
    isEnabled: Boolean,
    changeItem: (itemText: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var itemTextFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = itemText,
                selection = TextRange(itemText.length)
            )
        )
    }
    var hasModified by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var wasInFocus by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            try {
                focusRequester.requestFocus()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
    if (isEditing) {
        TextField(
            value = itemTextFieldValue,
            onValueChange = { newValue ->
                itemTextFieldValue = newValue
                if (newValue.text != itemText) {
                    hasModified = true
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            enabled = isEnabled,
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && wasInFocus) {
                        isEditing = false
                        if (hasModified) {
                            changeItem(itemTextFieldValue.text)
                            hasModified = false
                        }
                    }
                    wasInFocus = focusState.isFocused
                }
        )
    } else {
        Text(
            text = itemTextFieldValue.text,
            modifier = modifier
                .padding(horizontal = 12.dp)
                .clickable {
                    isEditing = true
                }
        )
    }
}

private class ItemPreviewProvider : PreviewParameterProvider<ItemState> {
    override val values: Sequence<ItemState> = sequenceOf(ItemState.mockItemLight, ItemState.mockItemDark)
}

@PreviewLightDark
@Composable
fun ShoppingListItemRowPreview(@PreviewParameter(ItemPreviewProvider::class) itemState: ItemState) {
    HamsterListTheme {
        ShoppingListItem(
            itemState = itemState,
            isEnabled = true,
            deleteItem = {},
            changeItem = { _ -> },
            showCategoryChooser = {}
        )
    }
}
