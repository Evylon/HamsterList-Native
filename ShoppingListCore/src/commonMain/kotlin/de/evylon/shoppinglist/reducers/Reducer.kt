package de.evylon.shoppinglist.reducers

import de.evylon.shoppinglist.utils.Cancellable
import de.evylon.shoppinglist.utils.collect
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

abstract class Reducer<A, S>(
    protected val coroutineScope: CoroutineScope,
    protected val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    abstract val uiStateFlow: StateFlow<S>
    abstract fun reduce(action: A)

    fun uiStateFlow(
        onEach: (S) -> Unit,
        onCompletion: (Throwable?) -> Unit
    ): Cancellable = uiStateFlow.collect(onEach, onCompletion)
}
