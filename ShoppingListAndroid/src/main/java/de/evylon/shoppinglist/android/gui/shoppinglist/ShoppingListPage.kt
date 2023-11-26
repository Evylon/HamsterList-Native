package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.reducers.LoadingState
import de.evylon.shoppinglist.reducers.shoppinglist.ShoppingListAction

@Composable
fun ShoppingListPage(shoppingListId: String) {
    val viewModel = ShoppingListViewModel()
    val uiState = viewModel.reducer.stateFlow.collectAsState()

    LaunchedEffect(shoppingListId) {
        viewModel.reducer.reduce(ShoppingListAction.FetchList(shoppingListId))
    }

    Crossfade(uiState.value.loadingState) { state ->
        when (state) {
            is LoadingState.Loading -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Loading...")
                }
            }
            is LoadingState.Error -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error")
                }
            }
            is LoadingState.Done -> {
                ShoppingListView(
                    shoppingList = uiState.value.shoppingList,
                    onDeleteItem = { item ->
                        viewModel.deleteItem(item)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ShoppingListPagePreview() {
    ShoppingListViewPreview()
}
