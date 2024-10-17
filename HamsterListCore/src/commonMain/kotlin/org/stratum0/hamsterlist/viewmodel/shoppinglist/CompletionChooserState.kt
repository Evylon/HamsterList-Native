package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.CompletionItem
import org.stratum0.hamsterlist.models.Item

data class CompletionChooserState(
    val filteredCompletions: List<CompletionItemState> = emptyList()
) {
    constructor(
        addItemInput: String,
        completions: List<CompletionItem>,
        categories: List<CategoryDefinition>
    ) : this(
        filteredCompletions = Item.parse(addItemInput, categories = categories).let { parsedItemInput ->
            completions
                .filter {
                    it.name.contains(parsedItemInput.name, ignoreCase = true)
                }
                .take(MAX_COMPLETIONS)
                .map { completion ->
                    CompletionItemState(
                        completion = completion,
                        categoryState = CategoryCircleState(categories.firstOrNull { it.id == completion.category })
                    )
                }
        }
    )

    companion object {
        private const val MAX_COMPLETIONS = 10

        val mock = CompletionChooserState(
            addItemInput = "",
            completions = listOf(
                CompletionItem.mockCompletionWithCategory,
                CompletionItem.mockCompletionWithoutCategory
            ),
            categories = listOf(
                CategoryDefinition.mockLight,
                CategoryDefinition.mockDark
            )
        )
    }
}
