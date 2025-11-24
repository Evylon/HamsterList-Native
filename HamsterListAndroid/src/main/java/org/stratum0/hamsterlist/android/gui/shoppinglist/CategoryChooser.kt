package org.stratum0.hamsterlist.android.gui.shoppinglist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.stratum0.hamsterlist.android.HamsterListTheme
import org.stratum0.hamsterlist.android.R
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.Item
import org.stratum0.hamsterlist.viewmodel.shoppinglist.CategoryChooserState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.CategoryCircleState
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ShoppingListAction

private const val MAX_DIALOG_HEIGHT = 600

@Composable
fun CategoryChooser(
    uiState: CategoryChooserState,
    onAction: (ShoppingListAction) -> Unit,
) {
    val onDismiss = { onAction(ShoppingListAction.DismissCategoryChooser) }
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .heightIn(max = MAX_DIALOG_HEIGHT.dp)
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.dialog_categoryChooser_headline),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp)
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    uiState.categories.forEach { category ->
                        CategoryListItem(
                            category = category,
                            selectedItem = uiState.selectedItem,
                            onAction = onAction,
                            dismiss = onDismiss
                        )
                        if (uiState.categories.last() != category) {
                            HorizontalDivider()
                        }
                    }
                }
                Row {
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(stringResource(R.string.dialog_dismiss_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryListItem(
    category: CategoryDefinition,
    selectedItem: Item,
    onAction: (ShoppingListAction) -> Unit,
    dismiss: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onAction(
                    ShoppingListAction.ChangeCategoryForItem(
                        item = selectedItem,
                        newCategoryId = category.id
                    )
                )
                dismiss()
            }
    ) {
        CategoryCircle(
            uiState = CategoryCircleState(category)
        )
        Text(
            text = category.name,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}

@PreviewLightDark
@Composable
fun CategoryChooserPreview() {
    HamsterListTheme {
        Surface {
            CategoryChooser(
                uiState = CategoryChooserState(
                    selectedItem = Item(
                        id = "UUID1",
                        name = "Item1",
                        category = "cat1"
                    ),
                    categories = listOf(
                        CategoryDefinition.mockLight,
                        CategoryDefinition.mockDark
                    ),
                ),
                onAction = {},
            )
        }
    }
}
