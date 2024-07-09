package de.evylon.shoppinglist.models

import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val value: Double,
    val unit: String? = null
) {
    override fun toString() = "$value${unit ?: ""}"
}
