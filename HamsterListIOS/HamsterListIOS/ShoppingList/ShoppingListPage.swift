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

    private var isLoading: Bool {
        uiState.value.loadingState is LoadingState.Loading || uiState.value.loadingState is LoadingState.Syncing
    }

    private var title: String {
        if (uiState.value.shoppingList.title.isEmpty) {
            hamsterList.listId
        } else {
            uiState.value.shoppingList.title
        }
    }

    let hamsterList: HamsterList

    init(hamsterList: HamsterList) {
        self.hamsterList = hamsterList
        self.viewModel = KoinViewModelHelper().shoppingListViewModel(hamsterList: hamsterList)
        self.uiState = FlowPublisher(
            publisher: createPublisher(for: self.viewModel.uiStateFlow),
            initial: ShoppingListState.companion.empty
        )
        uiState.subscribePublisher()
    }

    private func onAction(_ action: ShoppingListAction) {
        viewModel.handleAction(action: action)
    }
    
    var body: some View {
        VStack(spacing: 0) {
            switch uiState.value.loadingState {
            case LoadingState.Loading(), LoadingState.Done(), LoadingState.Syncing():
                    ZStack(alignment: .bottom) {
                        ShoppingListView(
                            shoppingListState: uiState.value,
                            deleteItem: { item in onAction(ShoppingListActionDeleteItem(item: item)) },
                            changeItem: { oldItem, newItem in onAction(ShoppingListActionChangeItem(oldItem: oldItem, newItem: newItem)) },
                            changeCategoryForItem: { item, newCategoryId in onAction(ShoppingListActionChangeCategoryForItem(item: item, newCategoryId: newCategoryId)) },
                            refresh: { onAction(ShoppingListActionFetchList()) }
                        )
                        if (!uiState.value.addItemInput.isEmpty) {
                            CompletionChooser(
                                uiState: uiState.value.completionChooserState,
                                addItem: { completion in
                                    onAction(ShoppingListActionAddItemByCompletion(completionItem: completion))
                                    onAction(ShoppingListActionUpdateAddItemInput(input: ""))
                                }
                            )
                            .padding(.top, 80)
                        }
                        if isLoading {
                            ProgressView()
                        }
                        LinearGradient(colors: [HamsterTheme.colors.background, Color.clear], startPoint: .bottom, endPoint: .top)
                            .frame(height: 12, alignment: .bottom)
                    }.allowsHitTesting(!isLoading)
                        .opacity(isLoading ? 0.5 : 1)
                    AddItemView(
                        newItem: .init(get: { uiState.value.addItemInput },
                                       set: { onAction(ShoppingListActionUpdateAddItemInput(input: $0)) }),
                        addItem: { newItem in
                            onAction(ShoppingListActionAddItem(input: newItem))
                        })
                case let error as LoadingState.Error:
                    ErrorContent(
                        throwable: error.throwable,
                        refresh: { onAction(ShoppingListActionFetchList()) }
                    )
                default:
                    Text("Should not happen")
            }
        }.onAppear {
            onAction(ShoppingListActionFetchList())
        }
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                if (!uiState.value.orders.isEmpty) {
                    ToolbarItem(placement: .topBarTrailing) {
                        OrderMenu(
                            orders: uiState.value.orders,
                            selectedOrder: uiState.value.selectedOrder,
                            selectOrder: { order in onAction(ShoppingListActionSelectOrder(order: order)) }
                        )
                    }
                }
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
