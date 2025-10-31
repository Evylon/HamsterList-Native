package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDefinition(
    val id: String,
    val name: String,
    val shortName: String,
    val color: String,
    val lightText: Boolean
) {
    val cssColor: CSSColor?
        get() = CSSColor.invoke(color)

    companion object {
        val mockLight = CategoryDefinition("", "cat1", "C1", "#32CD32", false)
        val mockDark = CategoryDefinition("", "cat2", "C2", "#000000", true)
    }
}
