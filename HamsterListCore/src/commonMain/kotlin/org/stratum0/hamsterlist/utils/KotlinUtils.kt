package org.stratum0.hamsterlist.utils

fun <T> T?.orDefault(default: T) = this ?: default
