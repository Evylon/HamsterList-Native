package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.gui.components.ShadowGradient
import org.stratum0.hamsterlist.viewmodel.LoadingState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListAction
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

@Composable
fun ShoppingListView(
    uiState: ShoppingListState,
    onAction: (ShoppingListAction) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    uiState.categoryChooserState?.let { chooserState ->
        CategoryChooser(
            uiState = chooserState,
            onAction = onAction,
        )
    }
    Column(
        modifier = modifier
            .padding(vertical = 12.dp)
            .animateContentSize()
    ) {
        if (uiState.orders.isNotEmpty()) {
            OrdersMenu(
                orders = uiState.orders,
                selectedOrder = uiState.selectedOrder,
                onAction = onAction,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
            )
        }
        Box(modifier = Modifier) {
            ShoppingItemsList(
                uiState = uiState,
                isEnabled = isEnabled,
                onAction = onAction,
                modifier = Modifier
            )
            if (uiState.addItemInput.isNotBlank()) {
                CompletionsChooser(
                    uiState = uiState.completionChooserState,
                    addItemByCompletion = { completion ->
                        onAction(ShoppingListAction.AddItemByCompletion(completion))
                        onAction(ShoppingListAction.UpdateAddItemInput(""))
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingItemsList(
    uiState: ShoppingListState,
    isEnabled: Boolean,
    onAction: (ShoppingListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    PullToRefreshBox(
        isRefreshing = uiState.loadingState is LoadingState.Loading,
        onRefresh = { onAction(ShoppingListAction.FetchList) },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            state = listState,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .animateContentSize()
        ) {
            items(
                uiState.shoppingList.items,
                key = { it.id }
            ) { item ->
                ShoppingListItem(
                    itemState = ItemState(
                        item = item,
                        categoryDefinition = ItemState.getCategory(item, uiState.categories)
                    ),
                    isEnabled = isEnabled,
                    onAction = onAction,
                )
            }
        }
        if (listState.canScrollBackward) {
            ShadowGradient(isTop = true)
        }
        if (listState.canScrollForward) {
            ShadowGradient(isTop = false)
        }
    }
}

@PreviewLightDark
@Composable
fun ShoppingListViewPreview() {
    HamsterListTheme {
        Surface {
            ShoppingListView(
                uiState = ShoppingListState.mock,
                onAction = {},
                isEnabled = true
            )
        }
    }
}
