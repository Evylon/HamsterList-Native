//
//  ShoppingListView.swift
//  HamsterListIOS
//
//  Created by David Hellmers on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import HamsterListCore
import SwiftUI

struct ShoppingListView: View {
    let shoppingListState: ShoppingListState

    let deleteItem: (Item) -> Void
    let changeItem: (_ id: String, _ newItem: String) -> Void
    let refresh: () -> Void

    @State private var newItem = ""

    var body: some View {
        // TODO for some reason the divider line is not symmetrical
        List(shoppingListState.shoppingList.items) { item in
            ShoppingListItem(
                itemState: ItemState(
                    item: item,
                    categoryDefinition: ItemState.companion.getCategory(
                        item: item,
                        categories: shoppingListState.categories
                    )
                ),
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
}

struct ShoppingListViewPreview: PreviewProvider {
    static var previews: some View {
        ShoppingListView(shoppingListState: ShoppingListState.companion.mock,
                         deleteItem: { _ in },
                         changeItem: { (_, _) in },
                         refresh: {}
        )
    }
}
