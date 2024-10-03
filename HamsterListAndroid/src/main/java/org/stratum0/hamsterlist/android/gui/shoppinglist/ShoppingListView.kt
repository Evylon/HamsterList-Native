package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.gui.components.ShadowGradient
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.viewmodel.LoadingState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("LongParameterList", "LongMethod")
fun ShoppingListView(
    uiState: ShoppingListState,
    deleteItem: (Item) -> Unit,
    changeItem: (oldItem: Item, newItem: String) -> Unit,
    changeCategoryForItem: (item: Item, newCategoryId: String) -> Unit,
    addItem: (item: String, completion: String?, category: String?) -> Unit,
    selectOrder: (Order) -> Unit,
    refresh: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.loadingState is LoadingState.Loading,
        onRefresh = refresh
    )
    var addItemInput by remember {
        mutableStateOf("")
    }
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
        Surface(color = MaterialTheme.colors.background) {
            Column {
                Text(
                    text = uiState.shoppingList.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
                OrdersMenu(
                    orders = uiState.orders,
                    selectedOrder = uiState.selectedOrder,
                    selectOrder = selectOrder,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .pullRefresh(pullRefreshState)
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
                            categoryChooserItem = item
                        },
                        changeItem = { itemText -> changeItem(item, itemText) },
                    )
                }
            }
            PullRefreshIndicator(
                refreshing = false,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                scale = true
            )
            if (addItemInput.isNotBlank()) {
                CompletionsChooser(
                    uiState = uiState,
                    userInput = addItemInput,
                    addItem = { item, completion, category ->
                        addItem(item, completion, category)
                        addItemInput = ""
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            ShadowGradient()
        }
        AddItemView(
            addItemInput = addItemInput,
            addItem = addItem,
            onItemInputChange = { addItemInput = it },
            isEnabled = isEnabled,
        )
    }
}

@Composable
private fun AddItemView(
    addItemInput: String,
    addItem: (item: String, completion: String?, category: String?) -> Unit,
    onItemInputChange: (input: String) -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = addItemInput,
                placeholder = { Text("New Item") },
                onValueChange = onItemInputChange,
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = {
                    if (addItemInput.isNotBlank()) {
                        addItem(addItemInput, null, null)
                        onItemInputChange("")
                        focusManager.clearFocus()
                    }
                }),
                enabled = isEnabled,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                enabled = isEnabled,
                onClick = {
                    // TODO display category suggestion and allow user to choose category
                    if (addItemInput.isNotBlank()) {
                        addItem(addItemInput, null, null)
                        onItemInputChange("")
                        focusManager.clearFocus()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add item",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ShoppingListViewPreview() {
    HamsterListTheme {
        Surface(color = MaterialTheme.colors.background) {
            ShoppingListView(
                uiState = ShoppingListState.mock,
                deleteItem = {},
                changeItem = { _, _ -> },
                changeCategoryForItem = { _, _ -> },
                selectOrder = {},
                isEnabled = true,
                addItem = { _, _, _ -> },
                refresh = {}
            )
        }
    }
}
