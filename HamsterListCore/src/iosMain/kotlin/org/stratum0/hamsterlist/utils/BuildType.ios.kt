package org.stratum0.hamsterlist.utils

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean
    get() = Platform.isDebugBinary