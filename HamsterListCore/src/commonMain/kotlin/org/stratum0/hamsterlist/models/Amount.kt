package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val value: Double,
    val unit: String? = null
) {
    override fun toString() = "$value${unit ?: ""}"

    companion object {
        fun parse(components: List<String>): Amount? {
            return when (components.size) {
                1 -> {
                    components[0].toDoubleOrNull()?.let {
                        Amount(value = it)
                    } ?: parseCombinedAmount(components[0])
                }
                2 -> {
                    components[0].toDoubleOrNull()?.let { value ->
                        components[1].takeIf { SIUnit.isSIUnit(it) }?.let { unit ->
                            Amount(value = value, unit = unit)
                        }
                    }
                }
                else -> null
            }
        }
        private fun parseCombinedAmount(input: String): Amount? {
            if (!Regex(PATTERN_COMBINED).matches(input)) {
                return null
            }
            val lastDigitIndex = input.indexOfLast { it.isDigit() }
            val value = input.substring(0, lastDigitIndex + 1).toDoubleOrNull()
            val unit = input.substring(lastDigitIndex + 1)
            return if (value != null && SIUnit.isSIUnit(unit)) {
                Amount(value, unit)
            } else {
                null
            }
        }
        private const val PATTERN_COMBINED = "^\\d+(\\.\\d+)?[a-zA-Z]+\$"
    }
}

enum class SIUnit {
    kg,
    L,
    m;
    companion object {
        // TODO add library for SI Unit parsing
        fun isSIUnit(input: String): Boolean =
            entries.any { input == it.name }
    }
}
