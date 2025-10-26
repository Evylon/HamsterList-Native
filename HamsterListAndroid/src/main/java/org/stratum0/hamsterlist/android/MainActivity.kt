package org.stratum0.hamsterlist.android

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
import androidx.compose.ui.Modifier
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
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val autoLoadLast = settingsRepository.autoLoadLast.value
        val lastListId = settingsRepository.loadedListId.value.orEmpty()
        setContent {
            HamsterListTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationHost(
                        autoLoadListId = lastListId.takeIf { autoLoadLast && lastListId.isNotBlank() }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationHost(autoLoadListId: String? = null) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomePage(
                uiState = uiState,
                onLoadHamsterList = { username, loadedList, autoLoadLast ->
                    viewModel.updateSettings(
                        newName = username,
                        loadedList = loadedList,
                        autoLoadLast = autoLoadLast
                    )
                    navController.navigate("shoppingList/${loadedList.listId}")
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
    autoLoadListId?.let {
        navController.navigate("shoppingList/$autoLoadListId")
    }
}
