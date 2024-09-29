//
//  HomePage.swift
//  HamsterListIOS
//
//  Created by David Hellmers on 04.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Combine
import KMPNativeCoroutinesCombine
import HamsterListCore
import SwiftUI

struct ShoppingListPage: View {
    private var viewModel: ShoppingListViewModel

    @ObservedObject
    private var uiState: FlowPublisher<ShoppingListState, Error>
    
    @State
    private var newItem = ""

    private var isLoading: Bool {
        uiState.value.loadingState is LoadingState.Loading
    }

    private var title: String {
        if (uiState.value.shoppingList.title.isEmpty) {
            listId
        } else {
            uiState.value.shoppingList.title
        }
    }

    let listId: String

    init(listId: String) {
        self.listId = listId
        self.viewModel = KoinViewModelHelper().shoppingListViewModel(listId: listId)
        self.uiState = FlowPublisher(
            publisher: createPublisher(for: self.viewModel.uiStateFlow),
            initial: ShoppingListState.companion.empty
        )
        uiState.subscribePublisher()
    }

    var body: some View {
        VStack {
            switch uiState.value.loadingState {
                case LoadingState.Loading(), LoadingState.Done():
                    ZStack {
                        ShoppingListView(
                            shoppingListState: uiState.value,
                            deleteItem: { item in viewModel.deleteItem(item: item) },
                            changeItemById: { id, newItem in viewModel.changeItem(id: id, newItem: newItem) },
                            changeCategoryForItem: { item, newCategoryId in viewModel.changeCategoryForItem(item: item, newCategoryId: newCategoryId) },
                            refresh: { viewModel.fetchList() }
                        )
                        if isLoading {
                            ProgressView()
                        }
                    }.allowsHitTesting(!isLoading)
                        .opacity(isLoading ? 0.5 : 1)
                case LoadingState.Error():
                    Text("Error")
                default:
                    Text("Should not happen")
            }
        }.onAppear {
            viewModel.fetchList()
        }
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                if (!uiState.value.orders.isEmpty) {
                    ToolbarItem(placement: .topBarTrailing) {
                        OrderMenu(
                            orders: uiState.value.orders,
                            selectedOrder: uiState.value.selectedOrder,
                            selectOrder: { order in viewModel.selectOrder(order: order) }
                        )
                    }
                }
                ToolbarItem(placement: .bottomBar) {
                    AddItemView
                }
            }
    }
    
    private var AddItemView: some View {
        HStack {
            TextField("New Item", text: $newItem)
                .textFieldStyle(BackgroundContrastStyle())
            Button(
                action: {
                    if (!newItem.isEmpty) {
                        viewModel.addItem(newItem: newItem)
                        newItem = ""
                    }
                },
                label: { Image(systemName: "plus") }
            ).tint(Color.primary)
        }
    }

    private func OrderMenu(
        orders: [Order],
        selectedOrder: Order?,
        selectOrder: @escaping (Order) -> Void
    ) -> some View {
        Menu {
            ForEach(orders) { order in
                Button(order.name) { selectOrder(order) }
            }
        } label: {
            Label(selectedOrder?.name ?? "Create Order", systemImage: "slider.horizontal.3")
        }
    }
}

struct ShoppingListPagePreview: PreviewProvider {
    static var previews: some View {
        NavigationStack {
            ShoppingListPage(listId: "Demo")
        }
    }
}
