package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.ShoppingListTheme
import de.evylon.shoppinglist.android.gui.utils.prettyFormat
import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.Item

@Composable
fun ShoppingListItemRow(
    item: Item,
    onDelete: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                modifier = Modifier.padding(8.dp).weight(1f)
            ) {
                item.amount?.let { AmountText(amount = it) }
                Text(item.name)
            }
            IconButton(onClick = { onDelete(item) }) {
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
private fun AmountText(
    amount: Amount,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            append(amount.value.prettyFormat())
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                amount.unit?.let { append(" $it") }
            }
        },
        modifier = modifier
    )
}

@Suppress("MagicNumber")
@Preview
@Composable
fun ShoppingListItemRowPreview() {
    ShoppingListTheme {
        ShoppingListItemRow(
            item = Item(
                id = "",
                name = "very long item title like really fucking long oh my god",
                amount = Amount(1337.42f, "kg"),
                category = "Category"
            ),
            onDelete = {}
        )
    }
}
