package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import org.stratum0.hamsterlist.android.gui.components.SyncIconButton
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
    Scaffold(
        topBar = {
            ShoppingListHeader(
                title = uiState.shoppingList.title,
                loadingState = uiState.loadingState,
                onBack = onBack,
                onRefresh = { onAction(ShoppingListAction.FetchList) }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ShoppingListContent(
                uiState = uiState,
                onAction = onAction,
                modifier = Modifier.weight(1f)
            )
            AddItemView(
                addItemInput = uiState.addItemInput,
                onAction = onAction,
                isEnabled = uiState.loadingState is LoadingState.Done,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListHeader(
    title: String,
    loadingState: LoadingState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigation_back_button),
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        actions = {
            SyncIconButton(
                loadingState = loadingState,
                onClick = onRefresh,
                modifier = Modifier.size(48.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
    )
}

@Composable
private fun ShoppingListContent(
    uiState: ShoppingListState,
    onAction: (ShoppingListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = uiState.loadingState,
        label = "loading state",
        modifier = modifier
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
            is LoadingState.SyncEnqueued,
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
}

@PreviewLightDark
@Composable
fun ShoppingListPagePreview() {
    HamsterListTheme {
        Surface {
            ShoppingListPage(
                uiState = ShoppingListState.mock,
                onAction = {},
                onBack = {}
            )
        }
    }
}
