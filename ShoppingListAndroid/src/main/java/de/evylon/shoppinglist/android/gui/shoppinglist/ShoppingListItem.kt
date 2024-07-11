package de.evylon.shoppinglist.android.gui.shoppinglist

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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.ShoppingListTheme
import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.Item

@Suppress("LongMethod")
@Composable
fun ShoppingListItem(
    item: Item,
    deleteItem: (Item) -> Unit,
    changeItem: (id: String, item: String) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {

    var itemText by remember { mutableStateOf(item.toString()) }
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
            focusRequester.requestFocus()
        }
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isEditing) {
                TextField(
                    value = itemTextFieldValue,
                    onValueChange = { newValue ->
                        itemTextFieldValue = newValue
                        if (newValue.text != itemText) {
                            itemText = newValue.text
                            hasModified = true
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    enabled = isEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && wasInFocus) {
                                isEditing = false
                                if (hasModified) {
                                    changeItem(item.itemId(), itemText)
                                    hasModified = false
                                }
                            }
                            wasInFocus = focusState.isFocused
                        }
                )
            } else {
                Text(
                    text = itemText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .clickable {
                            isEditing = true
                        }
                )
            }
            IconButton(
                enabled = isEnabled,
                onClick = { deleteItem(item) }
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

@Suppress("MagicNumber")
@Preview
@Composable
fun ShoppingListItemRowPreview() {
    ShoppingListTheme {
        ShoppingListItem(
            item = Item.Data(
                id = "",
                name = "very long item title like really fucking long oh my god",
                amount = Amount(1337.42, "kg"),
                category = "Category"
            ),
            deleteItem = {},
            changeItem = { _, _ -> },
            isEnabled = true
        )
    }
}
