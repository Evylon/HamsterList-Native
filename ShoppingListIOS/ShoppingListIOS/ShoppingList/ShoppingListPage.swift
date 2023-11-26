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
                switch viewModel.shoppingListState.loadingState {
                    case LoadingState.Done():
                        Text(viewModel.shoppingListState.shoppingList.title)
                        List(viewModel.shoppingListState.shoppingList.items) { item in
                            Text(item.description())
                                .swipeActions {
                                    Button(action: {
                                        viewModel.deleteItem(item: item)
                                    }) {
                                        Image(systemName: "trash")
                                    }.tint(Color.red)
                                }
                        }
                    case LoadingState.Loading():
                        Text("Loading...")
                    case LoadingState.Error():
                        Text("Error")
                    default:
                        Text("Should not happen")
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
