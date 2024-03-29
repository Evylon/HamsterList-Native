package de.evylon.shoppinglist.android.gui.shoppinglist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.ShoppingListTheme
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.viewmodel.shoppinglist.ShoppingListState

@Composable
fun ShoppingListView(
    shoppingList: ShoppingList,
    onDeleteItem: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = rememberLazyListState(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        modifier = modifier.padding(12.dp)
    ) {
        item {
            Text(
                text = shoppingList.title,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        shoppingList.items.forEach { item ->
            item {
                ShoppingListItemRow(
                    item = item,
                    onDelete = onDeleteItem
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, group = "light")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, group = "dark")
@Composable
fun ShoppingListViewPreview() {
    ShoppingListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ShoppingListView(ShoppingListState.mock.shoppingList, onDeleteItem = {})
        }
    }
}
