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
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.viewmodel.shoppinglist.CompletionChooserState

@Composable
fun CompletionsChooser(
    uiState: CompletionChooserState,
    addItemByCompletion: (completion: CompletionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.filteredCompletions.isNotEmpty()) {
        Card(modifier = modifier.fillMaxWidth()) {
            LazyColumn(reverseLayout = true) {
                items(
                    items = uiState.filteredCompletions,
                    key = { it.completion.name }
                ) { itemState ->
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            CategoryCircle(uiState = itemState.categoryState)
                            Text(
                                text = itemState.completion.name,
                                color = MaterialTheme.colors.onSurface,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        addItemByCompletion(itemState.completion)
                                    }
                            )
                        }
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
        CompletionsChooser(
            uiState = CompletionChooserState.mock,
            addItemByCompletion = {}
        )
    }
}
