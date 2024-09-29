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
    private let showCategoryChooser: () -> Void

    @State private var itemText: String

    init(
        itemState: ItemState,
        changeItem: @escaping (_ newItem: String) -> Void,
        showCategoryChooser: @escaping () -> Void
    ) {
        self.itemState = itemState
        self.itemText = itemState.item.description
        self.changeItem = changeItem
        self.showCategoryChooser = showCategoryChooser
    }

    var body: some View {
        HStack {
            // do not use Button https://www.hackingwithswift.com/quick-start/swiftui/how-to-control-the-tappable-area-of-a-view-using-contentshape
            CategoryCircle(uiState: itemState.categoryCircleState)
                .contentShape(Rectangle())
                .onTapGesture {
                    showCategoryChooser()
                }
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
        List {
            ShoppingListItem(
                itemState: ItemState.companion.mockItemLight,
                changeItem: { _ in },
                showCategoryChooser: {}
            )
            ShoppingListItem(
                itemState: ItemState.companion.mockItemDark,
                changeItem: { _ in },
                showCategoryChooser: {}
            )
        }
    }
}
