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
import KMPNativeCoroutinesAsync

struct ShoppingListPage: View {
    private let shoppingListRepository: ShoppingListRepository
    private var viewModel: ShoppingListViewModel

    let listId: String

    @State
    var uiState: ShoppingListState = ShoppingListState.companion.empty

    init(listId: String) {
        self.listId = listId
        let repository = ShoppingListRepositoryImpl()
        self.shoppingListRepository = repository
        self.viewModel = ShoppingListViewModel(shoppingListRepository: repository)
    }

    func listenToUiState() async {
        do {
            let sequence = asyncSequence(for: viewModel.uiStateFlowFlow)
            for try await uiState in sequence {
                self.uiState = uiState
            }
        } catch {
            print("sequence exception error: \(error)")
        }
    }

    var body: some View {
        NavigationView {
            VStack {
                switch uiState.loadingState {
                    case LoadingState.Done():
                        Text(uiState.shoppingList.title)
                        List(uiState.shoppingList.items) { item in
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
        }.task {
            await listenToUiState()
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
