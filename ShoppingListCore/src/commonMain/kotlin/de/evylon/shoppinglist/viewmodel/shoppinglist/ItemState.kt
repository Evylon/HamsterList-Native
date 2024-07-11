package de.evylon.shoppinglist.viewmodel.shoppinglist

import de.evylon.shoppinglist.models.Amount
import de.evylon.shoppinglist.models.CategoryDefinition
import de.evylon.shoppinglist.models.Item
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

data class ItemState(
    val item: Item,
    val itemText: String,
    val category: String,
    val categoryColor: Long,
    val categoryTextLight: Boolean,
    val isEnabled: Boolean,
) {
    @OptIn(ExperimentalStdlibApi::class)
    constructor(item: Item, categoryDefinition: CategoryDefinition?) : this(
        item = item,
        itemText = item.toString(),
        category = categoryDefinition?.shortName ?: DEFAULT_CATEGORY_TEXT,
        categoryColor = try {
            categoryDefinition?.colorCode?.hexToLong() ?: DEFAULT_CATEGORY_COLOR
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "failed to parse color" }
            DEFAULT_CATEGORY_COLOR
        },
        categoryTextLight = categoryDefinition?.lightText ?: false,
        isEnabled = false
    )

    companion object {
        private const val DEFAULT_CATEGORY_COLOR = 0xFFCCCCCC
        private const val DEFAULT_CATEGORY_TEXT = "?"
        val mockItemDark = ItemState(
            item = Item.Data(
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
            item = Item.Data(
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
