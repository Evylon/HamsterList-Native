package de.evylon.shoppinglist.models

import de.evylon.shoppinglist.utils.prettyFormat
import kotlinx.serialization.Serializable

@Serializable(ItemSerializer::class)
sealed class Item {
    @Serializable
    data class Text(
        val id: String,
        val stringRepresentation: String
    ) : Item() {
        override fun toString(): String = stringRepresentation
    }

    @Serializable
    data class Data(
        val id: String,
        val name: String,
        val amount: Amount? = null,
        val category: String? = null
    ) : Item() {
        override fun toString(): String = buildString {
            amount?.value?.let { value ->
                append(value.prettyFormat())
                amount.unit?.let { append(" ${it.trim()}") }
            }
            append(" ${name.trim()}")
        }
    }

    fun itemId(): String = when (this) {
        is Text -> this.id
        is Data -> this.id
    }
}

