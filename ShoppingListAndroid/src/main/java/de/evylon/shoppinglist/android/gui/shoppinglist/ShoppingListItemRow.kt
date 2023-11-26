package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.ShoppingListTheme
import de.evylon.shoppinglist.android.R
import de.evylon.shoppinglist.android.gui.utils.prettyFormat
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListState

@Composable
fun ShoppingListItemRow(item: Item, onDelete: (Item) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            modifier = Modifier.padding(8.dp)
        ) {
            item.amount?.let { amount ->
                Text(amount.value.prettyFormat())
                amount.unit?.let { unit ->
                    Text(unit, fontStyle = FontStyle.Italic)
                }
            }
            Text(item.name)
            Spacer(modifier = Modifier.weight(1.0f))
            Image(
                painter = painterResource(id = R.drawable.ic_delete),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.error),
                contentDescription = "Delete item",
                modifier = Modifier.clickable { onDelete(item) }
            )
        }
    }
}

@Preview
@Composable
fun ShoppingListItemRowPreview() {
    val mockShoppingList = ShoppingListState.Companion.mock.shoppingList
    ShoppingListTheme {
        ShoppingListItemRow(
            item = mockShoppingList.items.first(),
            onDelete = {}
        )
    }
}
