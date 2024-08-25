package org.stratum0.hamsterlist.android.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.business.ShoppingListRepository

@Composable
fun HomePage(onNavigateToShoppingList: (String) -> Unit) {
    var listId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        var username by remember {
            mutableStateOf(ShoppingListRepository.instance.username)
        }
        TextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Enter username") },
            modifier = Modifier.onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    ShoppingListRepository.instance.username = username
                }
            }
        )
        TextField(
            value = listId,
            onValueChange = { listId = it },
            placeholder = { Text("Enter list name") },
        )
        Button(
            onClick = { onNavigateToShoppingList(listId) },
            enabled = listId.isNotEmpty()
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
