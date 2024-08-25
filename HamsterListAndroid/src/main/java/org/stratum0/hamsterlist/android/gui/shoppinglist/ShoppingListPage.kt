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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.viewmodel.LoadingState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

const val ALPHA_LOADING = 0.5f

@Composable
fun ShoppingListPage(shoppingListId: String) {
    val viewModel by remember { mutableStateOf(ShoppingListViewModel()) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(shoppingListId) {
        viewModel.fetchList(shoppingListId)
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
                        deleteItem = { viewModel.deleteItem(it) },
                        changeItem = { id, item -> viewModel.changeItem(id, item) },
                        addItem = { item -> viewModel.addItem(item) },
                        selectOrder = { order -> viewModel.selectOrder(order) },
                        refresh = { viewModel.fetchList(listId = shoppingListId) },
                        isEnabled = !isLoading,
                        modifier = Modifier.alpha(ALPHA_LOADING).takeIf { isLoading } ?: Modifier
                    )
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ShoppingListPagePreview() {
    ShoppingListViewPreview()
}
