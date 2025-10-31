package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.viewmodel.shoppinglist.CategoryCircleState

private const val MAX_DIALOG_HEIGHT = 600

@Composable
fun CategoryChooser(
    selectedItem: Item,
    categories: List<CategoryDefinition>,
    changeCategoryForItem: (item: Item, newCategoryId: String) -> Unit,
    dismiss: () -> Unit
) {
    Dialog(onDismissRequest = dismiss) {
        Card {
            val verticalScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .heightIn(max = MAX_DIALOG_HEIGHT.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Choose a category",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                )
                Column(
                    modifier = Modifier.verticalScroll(verticalScrollState)
                ) {
                    categories.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    changeCategoryForItem(selectedItem, category.id)
                                    dismiss()
                                }
                        ) {
                            CategoryCircle(uiState = CategoryCircleState(category))
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                        }
                        if (categories.last() != category) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CategoryChooserPreview() {
    HamsterListTheme {
        Surface {
            CategoryChooser(
                selectedItem = Item(
                    id = "UUID1",
                    name = "Item1",
                    category = "cat1"
                ),
                categories = listOf(
                    CategoryDefinition.mockLight,
                    CategoryDefinition.mockDark
                ),
                changeCategoryForItem = { _, _ -> },
                dismiss = {}
            )
        }
    }
}
