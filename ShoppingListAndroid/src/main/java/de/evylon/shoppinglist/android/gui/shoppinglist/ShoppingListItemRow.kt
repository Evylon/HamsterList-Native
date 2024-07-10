package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.ShoppingListTheme
import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.Item

@Composable
fun ShoppingListItemRow(
    item: Item,
    deleteItem: (Item) -> Unit,
    changeItem: (id: String, item: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var itemText by remember {
        mutableStateOf(item.toString())
    }
    var hasModified by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = itemText,
                onValueChange = {
                    itemText = it
                    hasModified = true
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused && hasModified) {
                            changeItem(item.itemId(), itemText)
                            hasModified = false
                        }
                    }
            )
            IconButton(onClick = { deleteItem(item) }) {
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
        ShoppingListItemRow(
            item = Item.Data(
                id = "",
                name = "very long item title like really fucking long oh my god",
                amount = Amount(1337.42, "kg"),
                category = "Category"
            ),
            deleteItem = {},
            changeItem = { _, _ -> }
        )
    }
}
