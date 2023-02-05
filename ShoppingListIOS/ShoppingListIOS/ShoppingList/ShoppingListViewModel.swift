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

    @Published var shoppingList: ShoppingList?

    func subscribeToShoppingList() {
        collect(shoppingListRepository.shoppingListFlow)
            .completeOnFailure()
            .sink { [weak self] result in
                guard let result = result else {
                    return
                }
                switch result {
                    case let success as NetworkResultSuccess<ShoppingList>:
                        self?.shoppingList = success.value
                    case let failure as NetworkResultFailure<ShoppingList>:
                        print("Failure \(failure.throwable.description())")
                    default:
                        print("Not gonna happen")
                }
            }
            .store(in: &subscriptions)
    }

    func loadListById(listId: String) {
        shoppingListRepository.loadListById(id: listId) { error in
            DispatchQueue.main.async {
                if let error = error {
                    print(error)
                }
            }
        }
    }

    func deleteItem(item: Item) {
        guard let listId = shoppingList?.id else {
            return
        }
        shoppingListRepository.deleteItem(listId: listId, item: item) { error in
            DispatchQueue.main.async {
                if let error = error {
                    print(error)
                }
            }
        }
    }
}
