package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.Item

data class CompletionItemState(
    val completion: Item,
    val categoryState: CategoryCircleState
)
