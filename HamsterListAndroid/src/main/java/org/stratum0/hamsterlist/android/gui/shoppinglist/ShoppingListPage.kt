package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.models.Order
import org.stratum0.hamsterlist.viewmodel.LoadingState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

const val ALPHA_LOADING = 0.5f

@Suppress("LongParameterList")
@Composable
fun ShoppingListPage(
    uiState: ShoppingListState,
    fetchList: () -> Unit,
    deleteItem: (Item) -> Unit,
    addItem: (item: String) -> Unit,
    changeItemById: (id: String, item: String) -> Unit,
    changeCategoryForItem: (item: Item, newCategoryId: String) -> Unit,
    selectOrder: (Order) -> Unit
) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        fetchList()
    }

    Crossfade(
        targetState = uiState.loadingState,
        label = "loading state"
    ) { state ->
        when (state) {
            is LoadingState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error")
                }
            }

            is LoadingState.Done,
            is LoadingState.Loading -> {
                val isLoading = state is LoadingState.Loading
                Box(modifier = Modifier.fillMaxSize()) {
                    ShoppingListView(
                        uiState = uiState,
                        deleteItem = deleteItem,
                        changeItemById = changeItemById,
                        changeCategoryForItem = changeCategoryForItem,
                        addItem = addItem,
                        selectOrder = selectOrder,
                        refresh = fetchList,
                        isEnabled = !isLoading,
                        modifier = Modifier
                            .alpha(ALPHA_LOADING)
                            .takeIf { isLoading } ?: Modifier
                    )
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ShoppingListPagePreview() {
    ShoppingListViewPreview()
}
