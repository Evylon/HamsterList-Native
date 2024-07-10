//
//  ShoppingListItem.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import ShoppingListCore

struct ShoppingListItem : View {
    private let item: Item
    private let deleteItem: (Item) -> Void
    private let changeItem: (_ id: String, _ newItem: String) -> Void

    @State private var itemText: String

    init(item: Item,
         deleteItem: @escaping (Item) -> Void,
         changeItem: @escaping (_ id: String, _ newItem: String) -> Void) {
        self.item = item
        self.itemText = item.description
        self.deleteItem = deleteItem
        self.changeItem = changeItem
    }

    var body: some View {
        HStack {
            TextField(
                "",
                text: $itemText,
                onEditingChanged: { isEditing in
                    // TODO this is different from Android
                    if !isEditing && itemText != item.description {
                        changeItem(item.itemId(), itemText)
                    }
                }
            )
        }
    }
}

struct ShoppingListItemPreview: PreviewProvider {
    static var previews: some View {
        ShoppingListItem(item: ShoppingListState.companion.mock.shoppingList.items.first as! Item.Data,
                         deleteItem: { _ in },
                         changeItem: { (_, _) in })
        .padding(24)
    }
}
