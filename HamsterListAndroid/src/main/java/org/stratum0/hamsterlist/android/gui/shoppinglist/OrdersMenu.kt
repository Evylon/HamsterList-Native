package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListAction
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

@Composable
fun OrdersMenu(
    orders: List<Order>,
    selectedOrder: Order?,
    onAction: (ShoppingListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(8.dp)
                .clickable(enabled = orders.isNotEmpty()) { isExpanded = true }
        ) {
            Text(text = selectedOrder?.name ?: stringResource(R.string.orders_dropdown_placeholder))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.orders_dropdown_icon)
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            orders.forEach { order ->
                DropdownMenuItem(
                    text = {
                        Text(text = order.name)
                    },
                    onClick = {
                        onAction(ShoppingListAction.SelectOrder(order))
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun OrdersMenuPreview() {
    HamsterListTheme {
        Surface {
            OrdersMenu(
                orders = ShoppingListState.mock.orders,
                selectedOrder = ShoppingListState.mock.selectedOrder,
                onAction = {}
            )
        }
    }
}
