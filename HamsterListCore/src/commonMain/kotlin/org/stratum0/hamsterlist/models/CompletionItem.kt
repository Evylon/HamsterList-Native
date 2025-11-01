package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable
import org.stratum0.hamsterlist.utils.randomUUID

@Serializable
data class CompletionItem(
    val name: String,
    val category: String? = null
) {
    fun toItem() = Item(
        id = randomUUID(),
        name = name,
        category = category
    )
    companion object {
        val mockCompletionWithCategory = CompletionItem(
            name = "Testing",
            category = CategoryDefinition.mockLight.id
        )
        val mockCompletionWithoutCategory = CompletionItem(
            name = "Test 123",
            category = null
        )
    }
}
