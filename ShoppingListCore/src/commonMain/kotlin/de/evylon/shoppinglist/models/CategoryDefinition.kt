package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDefinition(
    val id: String,
    val name: String,
    val shortName: String,
    val color: String,
    val lightText: Boolean
) {
    val colorCode: String
        get() = "FF${color.substring(1)}"
}
