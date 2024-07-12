package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.ShoppingListTheme
import de.evylon.shoppinglist.models.Order
import de.evylon.shoppinglist.viewmodel.shoppinglist.ShoppingListState

@Composable
fun OrdersMenu(
    orders: List<Order>,
    selectedOrder: Order?,
    selectOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background)
                .border(width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(6.dp))
                .padding(8.dp)
                .clickable(enabled = orders.isNotEmpty()) { isExpanded = true }
        ) {
            Text(text = selectedOrder?.name ?: "Create Order")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Open Order selection"
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            orders.forEach { order ->
                DropdownMenuItem(onClick = {
                    selectOrder(order)
                    isExpanded = false
                }) {
                    Text(text = order.name)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun OrdersMenuPreview() {
    ShoppingListTheme {
        Surface {
            OrdersMenu(
                orders = ShoppingListState.mock.orders,
                selectedOrder = ShoppingListState.mock.selectedOrder,
                selectOrder = {}
            )
        }
    }
}
