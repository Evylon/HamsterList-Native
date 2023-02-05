package de.evylon.shoppinglist.android.gui.utils

import kotlin.math.roundToInt

fun Float.prettyFormat(): String = when {
    this.isDecimal -> this.toInt().toString()
    else -> "%.2f".format(this)
}

val Float.isDecimal: Boolean
    get() = this.roundToInt().toFloat() == this
