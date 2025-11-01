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
        val mockLight = CategoryDefinition("cat0", "cat0", "C0", "#32CD32", false)
        val mockDark = CategoryDefinition("cat1", "cat1", "C1", "#000000", true)
    }
}
