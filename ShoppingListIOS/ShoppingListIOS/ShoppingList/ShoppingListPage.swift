//
//  HomePage.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 04.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import ShoppingListCore
import SwiftUI

struct ShoppingListPage: View {

    @StateObject
    private var viewModel = ShoppingListViewModel()
    
    let listId: String

    init(listId: String) {
        self.listId = listId
    }

    var body: some View {
        NavigationView {
            VStack {
                if let shoppingList = viewModel.shoppingList {
                    Text(shoppingList.title)
                    List(shoppingList.items) { item in
                        Text(item.description())
                            .swipeActions {
                                Button(action: {
                                    viewModel.deleteItem(item: item)
                                }) {
                                    Image(systemName: "trash")
                                }.tint(Color.red)
                            }
                    }
                } else {
                    Text("Loading...")
                }
            }
        }.onAppear {
            print("init ViewModel")
            viewModel.subscribeToShoppingList()
            viewModel.loadListById(listId: listId)
        }
    }
}

struct ShoppingListPagePreview: PreviewProvider {
    static var previews: some View {
        ShoppingListPage(listId: "Demo")
    }
}
