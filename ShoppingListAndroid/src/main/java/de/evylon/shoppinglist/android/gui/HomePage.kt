package de.evylon.shoppinglist.android.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController

@Composable
fun HomePage(onNavigateToShoppingList: (String) -> Unit) {
    var listId by remember { mutableStateOf("Demo") }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = listId,
            onValueChange = { listId = it },
            modifier = Modifier.background(Color.White)
        )
        Button(onClick = {
            onNavigateToShoppingList(listId)
        }) {
            Text(text = "Load")
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    HomePage {}
}
