package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable
import org.stratum0.hamsterlist.utils.prettyFormat
import org.stratum0.hamsterlist.utils.randomUUID

@Serializable
data class Item(
    val id: String,
    val name: String,
    val amount: Amount? = null,
    val category: String? = null
) {
    companion object {
        fun parse(
            stringRepresentation: String,
            id: String = randomUUID(),
            category: String? = null,
            categories: List<CategoryDefinition>
        ): Item {
            val trimmedString = stringRepresentation.trim()
            // check if category can be parsed
            if (category == null) {
                val firstComponent = trimmedString.substringBefore(' ')
                if (firstComponent.startsWith('(') && firstComponent.endsWith(')')) {
                    val categoryShortName = firstComponent.substring(1, firstComponent.length - 1)
                    val parsedCategory = categories.firstOrNull { it.shortName == categoryShortName }
                    // if category was parsed sucessfully, recursively call parse with category now set
                    if (parsedCategory != null) return parse(
                        stringRepresentation = trimmedString.substringAfter(' '),
                        id = id,
                        category = parsedCategory.id,
                        categories = categories
                    )
                }
            }
            val components = trimmedString.split(' ', limit = 3)
            for (i in components.size downTo 1) {
                val amountCandidate = components.subList(0, i)
                Amount.parse(amountCandidate)?.let { amount ->
                    return Item(
                        id = id,
                        name = components
                            .subList(i, components.size)
                            .joinToString(separator = " ") { it },
                        amount = amount,
                        category = category
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

