package org.stratum0.hamsterlist.utils

import kotlin.math.roundToInt

@Suppress("MagicNumber")
fun Double.prettyFormat(): String = when {
    this.isDecimal -> this.toInt().toString()
    else -> {
        val integerPart = this.toInt()
        val firstTwoDecimals = ((this - integerPart) * 100).roundToInt()
        "$integerPart,$firstTwoDecimals"
    }
}

val Double.isDecimal: Boolean
    get() = this.roundToInt().toDouble() == this
