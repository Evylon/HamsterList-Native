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
    private var viewModel: ShoppingListViewModel

    @ObservedObject
    private var uiState: FlowPublisher<ShoppingListState, Error>
    
    @State private var newItem = ""
    
    private var isLoading: Bool {
        uiState.value.loadingState is LoadingState.Loading
    }

    let listId: String

    init(listId: String) {
        self.listId = listId
        self.viewModel = ShoppingListViewModel()
        self.uiState = FlowPublisher(
            publisher: createPublisher(for: self.viewModel.uiStateFlow),
            initial: ShoppingListState.companion.empty
        )
        uiState.subscribePublisher()
    }

    var body: some View {
        NavigationView {
            VStack {
                switch uiState.value.loadingState {
                    case LoadingState.Loading(), LoadingState.Done():
                        ZStack {
                            ShoppingListView(
                                shoppingList: uiState.value.shoppingList,
                                deleteItem: { item in viewModel.deleteItem(item: item) },
                                changeItem: { id, newItem in viewModel.changeItem(id: id, newItem: newItem) },
                                addItem: { newItem in viewModel.addItem(newItem: newItem) },
                                refresh: { viewModel.fetchList(listId: listId) }
                            ).allowsHitTesting(!isLoading)
                                .opacity(isLoading ? 0.5 : 1)
                            if isLoading {
                                ProgressView()
                            }
                        }
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
