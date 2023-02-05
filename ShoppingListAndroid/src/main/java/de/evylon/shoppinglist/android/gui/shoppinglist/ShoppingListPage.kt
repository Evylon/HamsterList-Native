package de.evylon.shoppinglist.android.gui.shoppinglist

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.evylon.shoppinglist.android.R
import de.evylon.shoppinglist.android.gui.utils.LoadingState
import de.evylon.shoppinglist.android.gui.utils.prettyFormat
import de.evylon.shoppinglist.business.ShoppingListRepositoryMock
import de.evylon.shoppinglist.models.Item
import de.evylon.shoppinglist.utils.NetworkResult

@Composable
fun ShoppingListPage(shoppingListId: String?) {
    val viewModel by remember {
        mutableStateOf(ShoppingListViewModel())
    }

    LaunchedEffect(shoppingListId) {
        viewModel.loadList(shoppingListId)
    }

    val listState = rememberLazyListState()

    Crossfade(viewModel.loadingState) { state ->
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
            is LoadingState.Done -> ShoppingListView(
                title = state.value.title,
                items = state.value.items,
                listState = listState,
                onDelete = { item ->
                    viewModel.deleteItem(state.value.id, item)
                })
        }
    }
}

@Preview
@Composable
fun ShoppingListPagePreview() {
    ShoppingListViewPreview()
}
