package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String,
    val name: String,
    val amount: Amount? = null,
    val category: String? = null
) {
    override fun toString() = "$amount $name"
}
