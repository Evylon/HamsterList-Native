//
//  ShoppingListItem.swift
//  HamsterListIOS
//
//  Created by David Hellmers on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import HamsterListCore

struct ShoppingListItem : View {
    private let itemState: ItemState
    private let changeItem: (_ newItem: String) -> Void

    @State private var itemText: String

    init(itemState: ItemState,
         changeItem: @escaping (_ newItem: String) -> Void) {
        self.itemState = itemState
        self.itemText = itemState.item.description
        self.changeItem = changeItem
    }

    var body: some View {
        HStack {
            ZStack(alignment: .center) {
                Circle()
                    .size(CGSize(width: 40, height: 40))
                    .fill(itemState.categoryColor.toColor())
                if (itemState.categoryTextLight) {
                    Text(itemState.category)
                        .foregroundStyle(.white)
                } else {
                    Text(itemState.category)
                        .foregroundStyle(.black)
                }
            }.frame(width: 40, height: 40)
            TextField(
                "",
                text: $itemText,
                onEditingChanged: { isEditing in
                    // TODO this is different from Android
                    if !isEditing && itemText != itemState.item.description() {
                        changeItem(itemText)
                    }
                }
            )
        }
    }
}

struct ShoppingListItemPreview: PreviewProvider {
    static var previews: some View {
        VStack {
            ShoppingListItem(
                itemState: ItemState.companion.mockItemLight,
                changeItem: { _ in }
            ).padding(24)
            ShoppingListItem(
                itemState: ItemState.companion.mockItemDark,
                changeItem: { _ in }
            ).padding(24)
        }
    }
}
