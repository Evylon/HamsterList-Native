package org.stratum0.hamsterlist.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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
import androidx.navigation.toRoute
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.stratum0.hamsterlist.android.gui.HomePage
import org.stratum0.hamsterlist.android.gui.shoppinglist.ShoppingListPage
import org.stratum0.hamsterlist.business.SettingsRepository
import org.stratum0.hamsterlist.business.SharedContentManager
import org.stratum0.hamsterlist.models.HamsterList
import org.stratum0.hamsterlist.viewmodel.home.HomeAction
import org.stratum0.hamsterlist.viewmodel.home.HomeViewModel
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListViewModel

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private val sharedContentManager: SharedContentManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val autoLoadLast = settingsRepository.autoLoadLast.value
        val lastLoadedList = settingsRepository.getKnownLists().firstOrNull()
        Log.d(this::class.simpleName, "lastList $lastLoadedList autoload $autoLoadLast")
        val hasSharedContent = handleTextSharing(intent)
        setContent {
            HamsterListTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavigationHost(
                        autoLoadList = lastLoadedList.takeIf { autoLoadLast && lastLoadedList != null },
                        hasSharedContentIntent = hasSharedContent,
                        modifier = Modifier
                    )
                }
            }
        }
    }

    private fun handleTextSharing(intent: Intent): Boolean {
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                sharedContentManager.enqueueSharedContent(sharedText)
                return true
            }
        }
        return false
    }
}

@Composable
fun NavigationHost(
    autoLoadList: HamsterList?,
    hasSharedContentIntent: Boolean,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var hasSharedContent by remember { mutableStateOf(hasSharedContentIntent) }
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        if (!hasSharedContentIntent && autoLoadList != null) {
            navController.navigate(autoLoadList)
        }
    }
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            val viewModel: HomeViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            if (hasSharedContent) {
                viewModel.handleHomeAction(HomeAction.OpenShareContentSheet)
                hasSharedContent = false
            }
            HomePage(
                uiState = uiState,
                onAction = viewModel::handleHomeAction,
                onLoadHamsterList = { selectedList ->
                    viewModel.handleHomeAction(
                        HomeAction.LoadHamsterlist(
                            selectedList = selectedList,
                            navigateToList = {
                                navController.navigate(selectedList)
                            }
                        )
                    )
                }
            )
        }
        composable<HamsterList> { backStackEntry ->
            val hamsterList: HamsterList = backStackEntry.toRoute()
            val viewModel: ShoppingListViewModel = koinViewModel { parametersOf(hamsterList) }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ShoppingListPage(
                uiState = uiState,
                onAction = viewModel::handleAction,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
