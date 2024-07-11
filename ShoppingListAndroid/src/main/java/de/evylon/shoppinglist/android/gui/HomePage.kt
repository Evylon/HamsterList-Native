package de.evylon.shoppinglist.android.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(onNavigateToShoppingList: (String) -> Unit) {
    var listId by remember { mutableStateOf("Demo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        TextField(
            value = listId,
            onValueChange = { listId = it },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = { onNavigateToShoppingList(listId) },
        ) {
            Text(text = "Load")
        }
    }
}

@Preview
@Composable
fun HomePagePreview() {
    Surface {
        HomePage {}
    }
}
