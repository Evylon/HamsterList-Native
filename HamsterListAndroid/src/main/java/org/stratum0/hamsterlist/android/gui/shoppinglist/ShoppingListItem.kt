package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.gui.utils.toColor
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState

@Suppress("LongMethod")
@Composable
fun ShoppingListItem(
    itemState: ItemState,
    deleteItem: (Item) -> Unit,
    changeItem: (itemText: String) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryCircle(
                category = itemState.category,
                categoryColor = itemState.categoryColor.toColor(),
                categoryTextLight = itemState.categoryTextLight
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
                    contentDescription = "Delete item",
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

@Composable
private fun CategoryCircle(
    category: String,
    categoryColor: Color,
    categoryTextLight: Boolean,
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .drawBehind {
                drawCircle(color = categoryColor)
            }
    ) {
        HamsterListTheme(darkTheme = categoryTextLight) {
            Text(
                text = category,
                color = HamsterListTheme.colors.primaryTextColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private class ItemPreviewProvider: PreviewParameterProvider<ItemState> {
    override val values: Sequence<ItemState> = sequenceOf(ItemState.mockItemLight, ItemState.mockItemDark)
}

@Suppress("MagicNumber")
@PreviewLightDark
@Composable
fun ShoppingListItemRowPreview(@PreviewParameter(ItemPreviewProvider::class) itemState: ItemState) {
    HamsterListTheme {
        ShoppingListItem(
            itemState = itemState,
            deleteItem = {},
            changeItem = { _ -> },
            isEnabled = true
        )
    }
}
