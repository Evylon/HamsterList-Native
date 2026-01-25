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
    let changeItem: (_ oldItem: Item, _ newItem: String) -> Void
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
                changeItem: { itemText in changeItem(item, itemText) },
                showCategoryChooser: { categoryChooserItem = item }
            ).swipeActions {
                Button(
                    role: .destructive,
                    action: { deleteItem(item) }
                ) {
                    Image(systemName: "trash")
                }
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

#Preview {
    ShoppingListView(
        shoppingListState: ShoppingListState.companion.mock,
        deleteItem: { _ in },
        changeItem: { (_, _) in },
        changeCategoryForItem: { _, _ in },
        refresh: {}
    )
}
