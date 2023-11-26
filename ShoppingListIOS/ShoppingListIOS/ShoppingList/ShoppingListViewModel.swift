//
//  ShoppingListViewModel.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 05.02.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Combine
import ShoppingListCore
import SwiftUI

class ShoppingListViewModel: ObservableObject {

    var subscriptions: Set<AnyCancellable> = Set()

    private let shoppingListRepository = ShoppingListRepositoryImpl()

    private let shoppingListReducer = ShoppingListReducerIos().proxy

    @Published var shoppingListState: ShoppingListState = ShoppingListState.companion.empty

    func subscribeToShoppingList() {
        collect(shoppingListReducer.uiStateFlow)
            .completeOnFailure()
            .sink { [weak self] result in
                guard let result = result else {
                    return
                }
                self?.shoppingListState = result
            }
            .store(in: &subscriptions)
    }

    func loadListById(listId: String) {
        shoppingListReducer.reduce(action: ShoppingListAction.FetchList(listId: listId))
    }

    func deleteItem(item: Item) {
        shoppingListReducer.reduce(action: ShoppingListAction.DeleteItem(item: item))
    }
}
