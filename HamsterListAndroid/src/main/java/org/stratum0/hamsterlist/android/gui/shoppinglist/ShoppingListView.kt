package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.android.gui.components.ShadowGradient
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
    addItem: (item: String, completion: String?, category: String?) -> Unit,
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
                    addItem = { completion, category ->
                        addItem(uiState.addItemInput, completion, category)
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
        )
    }
}

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
    Box(modifier = modifier
        .fillMaxSize()
    ) {
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
        if (listState.canScrollForward) {
            ShadowGradient(isTop = false)
        }
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(8.dp)
    ) {
        TextField(
            value = addItemInput,
            placeholder = {
                Text(stringResource(R.string.hamsterList_newItem_placeholder))
                          },
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
            modifier = Modifier.weight(1f).border(width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
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
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.hamsterList_newItem_icon),
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        )
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
                addItem = { _, _, _ -> },
                refresh = {},
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}
