package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.models.Item

data class CategoryChooserState(
    val selectedItem: Item,
    val categories: List<CategoryDefinition>
)
