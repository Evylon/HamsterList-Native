package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.gui.components.ShadowGradient
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.viewmodel.LoadingState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

@Composable
@Suppress("LongParameterList", "LongMethod")
fun ShoppingListView(
    uiState: ShoppingListState,
    updateAddItemInput: (String) -> Unit,
    deleteItem: (Item) -> Unit,
    changeItem: (oldItem: Item, newItem: String) -> Unit,
    changeCategoryForItem: (item: Item, newCategoryId: String) -> Unit,
    addItem: (itemInput: String) -> Unit,
    addItemByCompletion: (completion: CompletionItem) -> Unit,
    selectOrder: (Order) -> Unit,
    refresh: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var categoryChooserItem by remember {
        mutableStateOf<Item?>(null)
    }
    categoryChooserItem?.let { selectedItem ->
        CategoryChooser(
            selectedItem = selectedItem,
            categories = uiState.categories,
            changeCategoryForItem = changeCategoryForItem,
            dismiss = { categoryChooserItem = null }
        )
    }
    Column(modifier = modifier.fillMaxSize()) {
        Surface {
            Column(modifier = Modifier.animateContentSize()) {
                Text(
                    text = uiState.shoppingList.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                if (uiState.orders.isNotEmpty()) {
                    OrdersMenu(
                        orders = uiState.orders,
                        selectedOrder = uiState.selectedOrder,
                        selectOrder = selectOrder,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
        Box(
            modifier = Modifier.weight(1f)
        ) {
            ShoppingItemsList(
                uiState = uiState,
                isEnabled = isEnabled,
                deleteItem = deleteItem,
                changeItem = changeItem,
                showCategoryChooser = { item ->
                    categoryChooserItem = item
                },
                refresh = refresh
            )
            if (uiState.addItemInput.isNotBlank()) {
                CompletionsChooser(
                    uiState = uiState.completionChooserState,
                    addItemByCompletion = { completion ->
                        addItemByCompletion(completion)
                        updateAddItemInput("")
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
        AddItemView(
            addItemInput = uiState.addItemInput,
            addItem = addItem,
            onItemInputChange = updateAddItemInput,
            isEnabled = isEnabled,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Suppress("LongParameterList") // TODO switch to event channel
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ShoppingItemsList(
    uiState: ShoppingListState,
    isEnabled: Boolean,
    deleteItem: (Item) -> Unit,
    changeItem: (oldItem: Item, newItem: String) -> Unit,
    showCategoryChooser: (item: Item) -> Unit,
    refresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.loadingState is LoadingState.Loading,
        onRefresh = refresh
    )
    val listState = rememberLazyListState()
    Box(modifier = modifier.fillMaxSize()) {
        if (listState.canScrollBackward) {
            ShadowGradient(isTop = true)
        }
        LazyColumn(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            state = listState,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .pullRefresh(pullRefreshState)
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
                    deleteItem = deleteItem,
                    showCategoryChooser = {
                        showCategoryChooser(item)
                    },
                    changeItem = { itemText -> changeItem(item, itemText) }
                )
                if (uiState.shoppingList.items.last() == item) {
                    Spacer(modifier.height(8.dp))
                }
            }
        }
        PullRefreshIndicator(
            refreshing = false,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            scale = true
        )
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
                updateAddItemInput = {},
                deleteItem = {},
                changeItem = { _, _ -> },
                changeCategoryForItem = { _, _ -> },
                selectOrder = {},
                isEnabled = true,
                addItem = {},
                addItemByCompletion = {},
                refresh = {},
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}
