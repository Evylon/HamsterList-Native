//
//  CategoryChooser.swift
//  HamsterList
//
//  Created by David Hellmers on 29.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import HamsterListCore

struct CategoryChooser : View {
    let categories: [CategoryDefinition]
    let selectedItem: Item
    let changeCategoryForItem: (Item, String) -> Void
    let dismiss: () -> Void

    var body: some View {
        List(categories) { category in
            Button(action: {
                changeCategoryForItem(selectedItem, category.id)
                dismiss()
            }, label: {
                HStack {
                    CategoryCircle(uiState: CategoryCircleState(categoryDefinition: category))
                    Text(category.name)
                }
            })
        }
    }
}

#Preview {
    CategoryChooser(categories: [CategoryDefinition.companion.mockLight, CategoryDefinition.companion.mockDark],
                    selectedItem: Item(id: "", name: "Item1", amount: nil, category: nil),
                    changeCategoryForItem: { _, _ in },
                    dismiss: {})
}
