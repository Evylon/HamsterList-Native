package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
enum class AdditionalData {
    categories,
    orders,
    completions,
    changes
}
