package org.stratum0.hamsterlist.models

import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val value: Double,
    val unit: String? = null
) {
    override fun toString() = "$value${unit ?: ""}"

    companion object {
        private const val PATTERN_COMBINED = "^\\d+(\\.\\d+)?[a-zA-Z]+\$"

        fun parse(components: List<String>): Amount? {
            return when (components.size) {
                1 -> {
                    components[0].toDoubleOrNull()?.let {
                        Amount(value = it)
                    } ?: parseCombinedAmount(components[0])
                }
                2 -> {
                    components[0].toDoubleOrNull()?.let { value ->
                        parseUnitAndPrefix(components[1])?.let { unit ->
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
            val unit = parseUnitAndPrefix(input.substring(lastDigitIndex + 1))
            return if (value != null && unit != null) {
                Amount(value, unit)
            } else {
                null
            }
        }

        private fun parseUnitAndPrefix(input: String): String? {
            val onlyUnit = Unit.parse(input)
            val unitWithOutPrefix = Unit.parse(input.takeLast(1))
            val metricPrefix = MetricPrefix.parse(input.take(input.length - 1))
            return if (onlyUnit != null) {
                onlyUnit.symbol
            } else if (unitWithOutPrefix != null && metricPrefix != null) {
                metricPrefix.symbol + unitWithOutPrefix.symbol
            } else {
                null
            }
        }
    }
}

enum class Unit(val symbol: String) {
    GRAMM("g"),
    LITRE("L"),
    LITRE_LOWERCASE("l"),
    METER("m");

    companion object {
        fun parse(input: String) = entries.firstOrNull { it.symbol == input }
    }
}

enum class MetricPrefix(val symbol: String) {
    KILO("k"),
    HECTO("h"),
    DECA("da"),
    DECI("d"),
    CENTI("c"),
    MILLI("m");

    companion object {
        fun parse(input: String) = MetricPrefix.entries.firstOrNull { it.symbol == input }
    }
}
