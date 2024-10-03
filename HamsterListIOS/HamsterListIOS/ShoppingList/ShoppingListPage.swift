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
        VStack(spacing: 0) {
            switch uiState.value.loadingState {
                case LoadingState.Loading(), LoadingState.Done():
                    ZStack(alignment: .bottom) {
                        ShoppingListView(
                            shoppingListState: uiState.value,
                            deleteItem: { item in viewModel.deleteItem(item: item) },
                            changeItem: { oldItem, newItem in viewModel.changeItem(oldItem: oldItem, newItem: newItem) },
                            changeCategoryForItem: { item, newCategoryId in viewModel.changeCategoryForItem(item: item, newCategoryId: newCategoryId) },
                            refresh: { viewModel.fetchList() }
                        )
                        if (!newItem.isEmpty && !filteredCompletions.isEmpty) {
                            CompletionChooser
                                .padding(.top, 80)
                        }
                        if isLoading {
                            ProgressView()
                        }
                        LinearGradient(colors: [HamsterTheme.colors.background, Color.clear], startPoint: .bottom, endPoint: .top)
                            .frame(height: 12, alignment: .bottom)
                    }.allowsHitTesting(!isLoading)
                        .opacity(isLoading ? 0.5 : 1)
                    AddItemView
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
            }
    }

    private var parsedItem: Item {
        Item.companion.parse(stringRepresentation: newItem, categories: uiState.value.categories)
    }

    private var filteredCompletions: [CompletionItem] {
        uiState.value.completions.filter {
            $0.name.lowercased().contains(parsedItem.name.lowercased())
        }
    }
    
    func category(for completion: CompletionItem) -> CategoryDefinition? {
        return uiState.value.categories.first { $0.id == completion.category }
    }

    private var CompletionChooser: some View {
        // TODO Jesus SwiftUI Lists really don't like items being added to them. Performance is really bad
        // TODO also the List takes up too much space if few items are in it. Maybe use UIKit Stackview Instead :/
        VStack {
            Divider()
                .overlay(Color.gray)
            List {
                Section {
                    ForEach(filteredCompletions, id: \.name) {completion in
                        HStack {
                            CategoryCircle(
                                uiState: CategoryCircleState(categoryDefinition: category(for: completion))
                            )
                            // TODO ensure accessibility, i.e. by using button with custom Theme
                            Text(completion.name)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .onTapGesture {
                                    viewModel.addItem(newItem: newItem, completion: completion.name, category: completion.category)
                                    newItem = ""
                                }
                        }
                        .listRowSeparatorTint(HamsterTheme.colors.primary)
                    }
                } header: { Text("Suggestions") }
            }
            .padding(.top, -8)
        }
        .background(HamsterTheme.colors.background)
    }

    private var AddItemView: some View {
        HStack {
            TextField("New Item", text: $newItem)
                .textFieldStyle(BackgroundContrastStyle())
                .onSubmit {
                    if (!newItem.isEmpty) {
                        viewModel.addItem(newItem: newItem, completion: nil, category: nil)
                        newItem = ""
                    }
                }
            Button(
                action: {
                    if (!newItem.isEmpty) {
                        viewModel.addItem(newItem: newItem, completion: nil, category: nil)
                        newItem = ""
                    }
                },
                label: { Image(systemName: "plus") }
            ).tint(Color.primary)
                .padding(4)
        }.padding(8)
            .background(Color.gray.opacity(0.3))
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
