package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val name: String,
    val categoryOrder: List<String>
)
