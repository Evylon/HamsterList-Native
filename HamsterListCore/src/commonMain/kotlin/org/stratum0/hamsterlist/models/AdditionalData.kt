package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
enum class AdditionalData {
    categories,
    orders,
    completions,
    changes
}
