//
//  AddItemView.swift
//  HamsterList
//
//  Created by David Hellmers on 06.10.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct AddItemView: View {
    @Binding var newItem: String
    let addItem: (_ newItem: String) -> Void

    var body: some View {
        HStack {
            TextField("New Item", text: $newItem)
                .textFieldStyle(BackgroundContrastStyle())
                .onSubmit {
                    if (!newItem.isEmpty) {
                        addItem(newItem)
                        newItem = ""
                    }
                }
            Button(
                action: {
                    if (!newItem.isEmpty) {
                        addItem(newItem)
                        newItem = ""
                    }
                },
                label: { Image(systemName: "plus") }
            ).tint(Color.primary)
                .padding(4)
        }.padding(8)
            .background(Color.gray.opacity(0.3))
    }
}

#Preview {
    AddItemView(newItem: .constant("Input"),
                addItem: { _ in })
}
