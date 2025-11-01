package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.android.gui.components.ErrorContent
import org.stratum0.hamsterlist.android.gui.components.HamsterListLoadingIndicator
import org.stratum0.hamsterlist.viewmodel.LoadingState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListAction
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

const val ALPHA_LOADING = 0.5f

@Composable
fun ShoppingListPage(
    uiState: ShoppingListState,
    onAction: (ShoppingListAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        onAction(ShoppingListAction.FetchList)
    }
    BackHandler { onBack() }

    Column(modifier = modifier.fillMaxSize()) {
        ShoppingListHeader(
            title = uiState.shoppingList.title,
            onBack = onBack
        )
        Crossfade(
            targetState = uiState.loadingState,
            label = "loading state",
            modifier = Modifier.weight(1f)
        ) { state ->
            when (state) {
                is LoadingState.Error -> {
                    ErrorContent(
                        throwable = state.throwable,
                        refresh = { onAction(ShoppingListAction.FetchList) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }

                is LoadingState.Done,
                is LoadingState.Loading -> {
                    val isLoading = state is LoadingState.Loading
                    Box(modifier = Modifier.fillMaxSize()) {
                        ShoppingListView(
                            uiState = uiState,
                            onAction = onAction,
                            isEnabled = !isLoading,
                            modifier = Modifier
                                .alpha(ALPHA_LOADING)
                                .takeIf { isLoading } ?: Modifier
                        )
                        if (isLoading) {
                            HamsterListLoadingIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
        AddItemView(
            addItemInput = uiState.addItemInput,
            onAction = onAction,
            isEnabled = uiState.loadingState is LoadingState.Done,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun ShoppingListHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.navigation_back_button)
            )
        }
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 48.dp)
        )
    }
}

@PreviewLightDark
@Composable
fun ShoppingListPagePreview() {
    HamsterListTheme {
        Surface {
            ShoppingListPage(
                uiState = ShoppingListState.mock,
                onAction = {},
                onBack = {},
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}
