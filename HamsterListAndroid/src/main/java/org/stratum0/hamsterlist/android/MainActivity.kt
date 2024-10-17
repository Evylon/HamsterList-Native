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
import com.russhwolf.settings.ObservableSettings
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.stratum0.hamsterlist.android.gui.HomePage
import org.stratum0.hamsterlist.android.gui.shoppinglist.ShoppingListPage
import org.stratum0.hamsterlist.business.SettingsKey
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

class MainActivity : ComponentActivity() {
    private val settings: ObservableSettings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val autoLoadLast = settings.getBooleanOrNull(SettingsKey.AUTO_LOAD_LAST.name) ?: false
        val listId = settings.getStringOrNull(SettingsKey.CURRENT_LIST_ID.name).orEmpty()
        setContent {
            HamsterListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationHost(
                        autoLoadListId = listId.takeIf { autoLoadLast && listId.isNotBlank() }
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
                onLoadHamsterList = { username, hamsterListName, serverHostName, autoLoadLast ->
                    viewModel.updateSettings(
                        newName = username,
                        listId = hamsterListName,
                        serverHostName = serverHostName,
                        autoLoadLast = autoLoadLast
                    )
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
