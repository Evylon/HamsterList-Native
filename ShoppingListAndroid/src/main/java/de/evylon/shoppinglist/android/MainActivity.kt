package de.evylon.shoppinglist.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.evylon.shoppinglist.android.gui.HomePage
import de.evylon.shoppinglist.android.gui.shoppinglist.ShoppingListPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
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
            HomePage(
                onNavigateToShoppingList = { navController.navigate("shoppingList/$it") },
            )
        }
        composable("shoppingList/{id}") {
            ShoppingListPage(shoppingListId = it.arguments?.getString("id") ?: "")
        }
    }
}
