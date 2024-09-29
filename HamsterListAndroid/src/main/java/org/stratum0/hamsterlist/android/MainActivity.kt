package org.stratum0.hamsterlist.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.stratum0.hamsterlist.android.gui.HomePage
import org.stratum0.hamsterlist.android.gui.shoppinglist.ShoppingListPage
import org.stratum0.hamsterlist.viewmodel.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HamsterListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationHost()
                }
            }
        }
    }
}

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomePage(
                uiState = uiState,
                onLoadHamsterList = { username, hamsterListName ->
                    viewModel.setUsernameAndListId(newName = username, listId = hamsterListName)
                    navController.navigate("shoppingList/$hamsterListName")
                },
            )
        }
        composable("shoppingList/{id}") {
            val shoppingListId = it.arguments?.getString("id") ?: ""
            val viewModel: ShoppingListViewModel = koinViewModel { parametersOf(shoppingListId) }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ShoppingListPage(
                uiState = uiState,
                fetchList = viewModel::fetchList,
                deleteItem = viewModel::deleteItem,
                addItem = viewModel::addItem,
                changeItemById = viewModel::changeItem,
                changeCategoryForItem = viewModel::changeCategoryForItem,
                selectOrder = viewModel::selectOrder
            )
        }
    }
}
