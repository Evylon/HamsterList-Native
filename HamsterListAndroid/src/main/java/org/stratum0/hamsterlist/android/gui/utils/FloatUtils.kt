package org.stratum0.hamsterlist.android.gui.utils

import kotlin.math.roundToInt

fun Double.prettyFormat(): String = when {
    this.isDecimal -> this.toInt().toString()
    else -> "%.2f".format(this)
}

val Double.isDecimal: Boolean
    get() = this.roundToInt().toDouble() == this
