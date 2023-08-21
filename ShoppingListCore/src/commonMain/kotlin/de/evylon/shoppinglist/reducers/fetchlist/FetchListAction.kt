package de.evylon.shoppinglist.reducers.fetchlist

sealed class FetchListAction {
    class FetchList(val listId: String) : FetchListAction()
}
