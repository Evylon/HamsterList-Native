package org.stratum0.hamsterlist.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.stratum0.hamsterlist.android.gui.HomePage
import org.stratum0.hamsterlist.android.gui.shoppinglist.ShoppingListPage
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.business.ShoppingListRepository
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private val shoppingListRepository: ShoppingListRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val autoLoadLast = settingsRepository.autoLoadLast.value
        val lastListId = settingsRepository.loadedListId.value.orEmpty()
        val hasSharedContent = handleTextSharing(intent)
        setContent {
            HamsterListTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                ) {
                    NavigationHost(
                        autoLoadListId = lastListId.takeIf { autoLoadLast && lastListId.isNotBlank() },
                        hasSharedContentIntent = hasSharedContent
                    )
                }
            }
        }
    }

    private fun handleTextSharing(intent: Intent): Boolean {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                shoppingListRepository.enqueueSharedContent(sharedText)
                return true
            }
        }
        return false
    }
}

@Composable
fun NavigationHost(
    autoLoadListId: String?,
    hasSharedContentIntent: Boolean
) {
    val navController = rememberNavController()
    var hasSharedContent by remember { mutableStateOf(hasSharedContentIntent) }
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        if (!hasSharedContent && autoLoadListId != null) {
            navController.navigate("shoppingList/$autoLoadListId")
        }
    }
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomePage(
                uiState = uiState,
                hasSharedContent = hasSharedContent,
                onLoadHamsterList = { username, loadedList, autoLoadLast ->
                    viewModel.updateSettings(
                        newName = username,
                        loadedList = loadedList,
                        autoLoadLast = autoLoadLast
                    )
                    navController.navigate("shoppingList/${loadedList.listId}")
                    hasSharedContent = false
                },
                onDeleteHamsterList = viewModel::deleteKnownList
            )
        }
        composable("shoppingList/{id}") {
            val shoppingListId = it.arguments?.getString("id") ?: ""
            val viewModel: ShoppingListViewModel = koinViewModel { parametersOf(shoppingListId) }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ShoppingListPage(
                uiState = uiState,
                updateAddItemInput = viewModel::updateAddItemInput,
                fetchList = viewModel::fetchList,
                deleteItem = viewModel::deleteItem,
                addItem = viewModel::addItem,
                changeItem = viewModel::changeItem,
                changeCategoryForItem = viewModel::changeCategoryForItem,
                selectOrder = viewModel::selectOrder
            )
        }
    }
}
