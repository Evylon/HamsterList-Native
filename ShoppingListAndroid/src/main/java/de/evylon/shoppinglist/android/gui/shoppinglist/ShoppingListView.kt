package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.models.ShoppingList
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListState

@Composable
fun ShoppingListView(
    shoppingList: ShoppingList,
    onDeleteItem: (Item) -> Unit
) {
    LazyColumn(
        state = rememberLazyListState(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        modifier = Modifier
            .padding(12.dp)
    ) {
        item {
            Text(shoppingList.title, Modifier.padding(bottom = 8.dp))
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

@Preview
@Composable
fun ShoppingListViewPreview() {
    Surface(modifier = Modifier.background(Color.White)) {
        ShoppingListView(ShoppingListState.mock.shoppingList, onDeleteItem = {})
    }
}
