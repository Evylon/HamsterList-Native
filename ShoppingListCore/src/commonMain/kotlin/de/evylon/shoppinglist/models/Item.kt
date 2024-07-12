package de.evylon.shoppinglist.models

import de.evylon.shoppinglist.utils.prettyFormat
import de.evylon.shoppinglist.utils.randomUUID
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String,
    val name: String,
    val amount: Amount? = null,
    val category: String? = null
) {
    companion object {
        // TODO add automatic category assignment
        fun parse(
            stringRepresentation: String,
            id: String = randomUUID()
        ): Item {
            val components = stringRepresentation.trim().split(' ', limit = 3)
            for (i in components.size downTo 1) {
                val amountCandidate = components.subList(0, i)
                Amount.parse(amountCandidate)?.let {
                    return Item(
                        id = id,
                        name = components
                            .subList(i, components.size)
                            .joinToString(separator = " ") { it },
                        amount = it
                    )
                }
            }
            return Item(
                id = id,
                name = components.joinToString(separator = " ") { it }
            )
        }
    }

    override fun toString(): String = buildString {
        amount?.value?.let { value ->
            append(value.prettyFormat())
            amount.unit?.let { append(" ${it.trim()}") }
        }
        append(" ${name.trim()}")
    }
}

