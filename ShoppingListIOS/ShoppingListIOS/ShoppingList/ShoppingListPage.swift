//
//  HomePage.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 04.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import KMPNativeCoroutinesCombine
import ShoppingListCore
import SwiftUI

struct ShoppingListPage: View {
    private let shoppingListRepository: ShoppingListRepository
    private var viewModel: ShoppingListViewModel

    @ObservedObject
    private var uiState: FlowPublisher<ShoppingListState, Error>

    let listId: String

    init(listId: String) {
        self.listId = listId
        let repository = ShoppingListRepositoryImpl()
        self.shoppingListRepository = repository
        self.viewModel = ShoppingListViewModel(shoppingListRepository: repository)
        self.uiState = FlowPublisher(
            publisher: createPublisher(for: self.viewModel.uiStateFlowFlow),
            initial: ShoppingListState.companion.empty
        )
        uiState.subscribePublisher()
    }

    var body: some View {
        NavigationView {
            VStack {
                switch uiState.value.loadingState {
                    case LoadingState.Done():
                        Text(uiState.value.shoppingList.title)
                        List(uiState.value.shoppingList.items) { item in
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
            viewModel.fetchList(listId: listId)
        }
    }
}


struct ShoppingListPagePreview: PreviewProvider {
    static var previews: some View {
        ShoppingListPage(listId: "Demo")
    }
}
