package de.evylon.shoppinglist.viewmodel.shoppinglist

import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.CSSColor
import de.evylon.shoppinglist.models.CategoryDefinition
import de.evylon.shoppinglist.models.Item

data class ItemState(
    val item: Item,
    val category: String,
    val categoryColor: CSSColor,
    val categoryTextLight: Boolean,
    val isEnabled: Boolean,
) {
    constructor(item: Item, categoryDefinition: CategoryDefinition?) : this(
        item = item,
        category = categoryDefinition?.shortName ?: DEFAULT_CATEGORY_TEXT,
        categoryColor = categoryDefinition?.cssColor ?: DEFAULT_CATEGORY_COLOR,
        categoryTextLight = categoryDefinition?.lightText ?: false,
        isEnabled = false
    )

    companion object {
        val DEFAULT_CATEGORY_COLOR = CSSColor.RGBAColor(alpha = 0xFF, red = 0xCC, green = 0xCC, blue = 0xCC)
        private const val DEFAULT_CATEGORY_TEXT = "?"

        fun getCategory(item: Item, categories: List<CategoryDefinition>) =
            categories.firstOrNull { it.id == item.category }

        val mockItemDark = ItemState(
            item = Item(
                id = "",
                name = "very long item title like really fucking long oh my god",
                amount = Amount(1337.42, "kg"),
                category = "1"
            ),
            categoryDefinition = CategoryDefinition(
                id = "1",
                name = "Category1",
                shortName = "C1",
                color = "#AAAAAA",
                lightText = true
            )
        )
        val mockItemLight = ItemState(
            item = Item(
                id = "",
                name = "short item name",
                amount = Amount(0.0),
                category = "2"
            ),
            categoryDefinition = CategoryDefinition(
                id = "2",
                name = "Category2",
                shortName = "C2",
                color = "#FFEB3B",
                lightText = false
            )
        )
    }
}
