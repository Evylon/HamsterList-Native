//
//  ShoppingListView.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import ShoppingListCore
import SwiftUI

struct ShoppingListView: View {
    private let shoppingListState: ShoppingListState

    private let deleteItem: (Item) -> Void
    private let changeItem: (_ id: String, _ newItem: String) -> Void
    private let addItem: (_ newItem: String) -> Void
    private let selectOrder: (Order) -> Void
    private let refresh: () -> Void

    @State private var newItem = ""

    init(shoppingListState: ShoppingListState,
         deleteItem: @escaping (Item) -> Void,
         changeItem: @escaping (_ id: String, _ newItem: String) -> Void,
         addItem: @escaping (_ newItem: String) -> Void,
         selectOrder: @escaping (Order) -> Void,
         refresh: @escaping () -> Void
    ) {
        self.shoppingListState = shoppingListState
        self.deleteItem = deleteItem
        self.changeItem = changeItem
        self.addItem = addItem
        self.selectOrder = selectOrder
        self.refresh = refresh
    }

    var body: some View {
        NavigationView {
            VStack {
                Text(shoppingListState.shoppingList.title)
                if (!shoppingListState.orders.isEmpty) {
                    OrderMenu(
                        orders: shoppingListState.orders,
                        selectedOrder: shoppingListState.selectedOrder,
                        selectOrder: selectOrder
                    )
                }
                ShoppingItemList(items: shoppingListState.shoppingList.items)
                AddItemView
            }
        }
    }

    private func ShoppingItemList(items: [Item]) -> some View {
        // TODO for some reason the divider line is not symmetrical
        List(items) { item in
            ShoppingListItem(
                itemState: ItemState(
                    item: item,
                    categoryDefinition: ItemState.companion.getCategory(
                        item: item, 
                        categories: shoppingListState.categories
                    )
                ),
                deleteItem: deleteItem,
                changeItem: { itemText in changeItem(item.id, itemText) }
            ).swipeActions {
                Button(action: {
                    deleteItem(item)
                }) {
                    Image(systemName: "trash")
                }.tint(Color.red)
            }
        }
        .refreshable { refresh() }
    }

    private var AddItemView: some View {
        HStack {
            TextField("New Item", text: $newItem)
                .textFieldStyle(BackgroundContrastStyle())
            Button(
                action: {
                    if (!newItem.isEmpty) {
                        addItem(newItem)
                        newItem = ""
                    }
                },
                label: { Image(systemName: "plus") }
            ).tint(Color.primary)
        }.padding(
            EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16)
        )
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

struct ShoppingListViewPreview: PreviewProvider {
    static var previews: some View {
        ShoppingListView(shoppingListState: ShoppingListState.companion.mock,
                         deleteItem: { _ in },
                         changeItem: { (_, _) in },
                         addItem: { _ in },
                         selectOrder: { _ in },
                         refresh: {}
        )
    }
}
