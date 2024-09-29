package org.stratum0.hamsterlist.viewmodel.shoppinglist

import org.stratum0.hamsterlist.models.CSSColor
import org.stratum0.hamsterlist.models.CategoryDefinition
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState.Companion.DEFAULT_CATEGORY_COLOR
import org.stratum0.hamsterlist.viewmodel.shoppinglist.ItemState.Companion.DEFAULT_CATEGORY_TEXT

data class CategoryCircleState(
    val category: String = "",
    val categoryColor: CSSColor = DEFAULT_CATEGORY_COLOR,
    val categoryTextLight: Boolean = false,
) {
    constructor(categoryDefinition: CategoryDefinition?) : this(
        category = categoryDefinition?.shortName ?: DEFAULT_CATEGORY_TEXT,
        categoryColor = categoryDefinition?.cssColor ?: DEFAULT_CATEGORY_COLOR,
        categoryTextLight = categoryDefinition?.lightText ?: false,
    )
}
