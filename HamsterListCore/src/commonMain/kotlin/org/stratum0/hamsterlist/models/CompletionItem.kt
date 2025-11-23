package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class CompletionItem(
    val name: String,
    val category: String? = null
) {
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
