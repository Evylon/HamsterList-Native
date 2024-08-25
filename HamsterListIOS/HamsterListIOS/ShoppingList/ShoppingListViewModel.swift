//
//  ShoppingListViewModel.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 05.02.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Combine
import HamsterListCore
import SwiftUI

class ShoppingListViewModel: ObservableObject {

    func loadListById(listId: String) {
        shoppingListReducer.reduce(action: ShoppingListAction.FetchList(listId: listId))
    }

    func deleteItem(item: Item) {
        shoppingListReducer.reduce(action: ShoppingListAction.DeleteItem(item: item))
    }
}
