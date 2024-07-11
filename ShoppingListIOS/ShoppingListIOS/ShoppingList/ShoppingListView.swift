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
    private let shoppingList: SyncedShoppingList

    private let deleteItem: (Item) -> Void
    private let changeItem: (_ id: String, _ newItem: String) -> Void
    private let addItem: (_ newItem: String) -> Void
    private let refresh: () -> Void

    @State private var newItem = ""

    init(shoppingList: SyncedShoppingList,
         deleteItem: @escaping (Item) -> Void,
         changeItem: @escaping (_ id: String, _ newItem: String) -> Void,
         addItem: @escaping (_ newItem: String) -> Void,
         refresh: @escaping () -> Void
    ) {
        self.shoppingList = shoppingList
        self.deleteItem = deleteItem
        self.changeItem = changeItem
        self.addItem = addItem
        self.refresh = refresh
    }

    var body: some View {
        NavigationView {
            VStack {
                Text(shoppingList.title)
                ShoppingItemList(items: shoppingList.items)
                AddItemView
            }
        }
    }

    private func ShoppingItemList(items: [Item]) -> some View {
        List(items) { item in
            ShoppingListItem(item: item,
                             deleteItem: deleteItem,
                             changeItem: changeItem
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
}

struct ShoppingListViewPreview: PreviewProvider {
    static var previews: some View {
        ShoppingListView(shoppingList: ShoppingListState.companion.mock.shoppingList,
                         deleteItem: { _ in },
                         changeItem: { (_, _) in },
                         addItem: { _ in },
                         refresh: {}
        ).padding(24)
    }
}
