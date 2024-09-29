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
    let changeItemById: (_ id: String, _ newItem: String) -> Void
    let changeCategoryForItem: (_ item: Item, _ newCategoryId: String) -> Void
    let refresh: () -> Void

    @State private var newItem = ""

    @State
    private var categoryChooserItem: Item? = nil

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
                changeItem: { itemText in changeItemById(item.id, itemText) },
                showCategoryChooser: { categoryChooserItem = item }
            ).swipeActions {
                Button(action: {
                    deleteItem(item)
                }) {
                    Image(systemName: "trash")
                }.tint(Color.red)
            }
        }
        .refreshable { refresh() }
        .sheet(
            item: $categoryChooserItem,
            onDismiss: { categoryChooserItem = nil },
            content: { selectedItem in
                CategoryChooser(categories: shoppingListState.categories,
                                selectedItem: selectedItem,
                                changeCategoryForItem: changeCategoryForItem,
                                dismiss: { self.categoryChooserItem = nil })
        })
    }
}

struct ShoppingListViewPreview: PreviewProvider {
    static var previews: some View {
        ShoppingListView(shoppingListState: ShoppingListState.companion.mock,
                         deleteItem: { _ in },
                         changeItemById: { (_, _) in },
                         changeCategoryForItem: { _, _ in },
                         refresh: {}
        )
    }
}
