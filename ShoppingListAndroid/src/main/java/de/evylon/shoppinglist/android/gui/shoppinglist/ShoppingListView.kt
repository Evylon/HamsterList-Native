package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.business.ShoppingListRepositoryMock
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.utils.NetworkResult

@Composable
fun ShoppingListView(
    title: String,
    items: List<Item>,
    listState: LazyListState,
    onDelete: (Item) -> Unit
) {
    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        modifier = Modifier
            .padding(12.dp)
    ) {
        item {
            Text(title, Modifier.padding(bottom = 8.dp))
        }
        items.forEach { item ->
            item {
                ShoppingListItemRow(item, onDelete)
            }
        }
    }
}

@Preview
@Composable
fun ShoppingListViewPreview() {
    val mockShoppingList = (ShoppingListRepositoryMock().shoppingListFlow.value as NetworkResult.Success).value
    Surface(modifier = Modifier.background(Color.White)) {
        ShoppingListView(
            title = mockShoppingList.title,
            items = mockShoppingList.items,
            listState = rememberLazyListState(),
            onDelete = {}
        )
    }
}
