package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.viewmodel.shoppinglist.CategoryCircleState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListState

@Composable
fun CompletionsChooser(
    uiState: ShoppingListState,
    userInput: String,
    addItem: (item: String, completion: String, category: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val parsedItem = Item.parse(userInput, categories = uiState.categories)
    val filteredCompletions = uiState.completions.filter { it.name.contains(parsedItem.name) }
    Card(modifier = modifier.fillMaxWidth()) {
        LazyColumn() {
            items(
                items = filteredCompletions,
                key = { it.name }
            ) { completion ->
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        val category = uiState.categories.firstOrNull { it.id == completion.category }
                        CategoryCircle(uiState = CategoryCircleState(category))
                        Text(
                            text = completion.name,
                            color = HamsterListTheme.colors.primaryTextColor,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    addItem(userInput, completion.name, completion.category)
                                }
                        )
                    }
                    if (filteredCompletions.last() != completion) {
                        Divider(
                            color = MaterialTheme.colors.primary,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CompletionsChooserPreview() {
    HamsterListTheme {
        CompletionsChooser(uiState = ShoppingListState.mock, userInput = "Te", addItem = { _, _, _ -> })
    }
}
