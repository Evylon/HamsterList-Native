package org.stratum0.hamsterlist.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual open class BaseViewModel {
    actual val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
