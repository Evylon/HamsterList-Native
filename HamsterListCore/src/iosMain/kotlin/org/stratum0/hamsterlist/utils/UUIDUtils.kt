package org.stratum0.hamsterlist.utils

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
