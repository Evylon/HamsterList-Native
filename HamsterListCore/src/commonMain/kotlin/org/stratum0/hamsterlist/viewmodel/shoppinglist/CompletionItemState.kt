package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.CompletionItem

data class CompletionItemState(
    val completion: CompletionItem,
    val categoryState: CategoryCircleState
)
