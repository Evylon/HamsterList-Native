package de.evylon.shoppinglist.utils

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
