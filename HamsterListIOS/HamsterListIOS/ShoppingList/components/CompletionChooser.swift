//
//  CompletionChooser.swift
//  HamsterList
//
//  Created by David Hellmers on 06.10.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import HamsterListCore

struct CompletionChooser: View {
    
    @Binding var newItem: String
    var completions: [CompletionItem]
    var categories: [CategoryDefinition]
    let addItem: (_ newItem: String, _ completion: String, _ category: String?) -> Void

    private var parsedItem: Item {
        Item.companion.parse(stringRepresentation: newItem, categories: categories)
    }

    private var filteredCompletions: [CompletionItem] {
        completions.filter {
            $0.name.lowercased().contains(parsedItem.name.lowercased())
        }
    }

    func resolveCategory(for completion: CompletionItem) -> CategoryDefinition? {
        return categories.first { $0.id == completion.category }
    }

    // Performance is really bad on Debug builds, but seems fine on release builds
    // TODO the List takes up too much space if few items are in it. Maybe use UIKit Stackview Instead :/
    var body: some View {
        if (!filteredCompletions.isEmpty) {
            VStack {
                Divider()
                    .overlay(Color.gray)
                List {
                    Section {
                        ForEach(filteredCompletions) { completion in
                            HStack {
                                CategoryCircle(
                                    uiState: CategoryCircleState(categoryDefinition: resolveCategory(for: completion))
                                )
                                // TODO ensure accessibility, i.e. by using button with custom Theme
                                Text(completion.name)
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    .onTapGesture {
                                        addItem(newItem, completion.name, completion.category)
                                        newItem = ""
                                    }
                            }
                            .listRowSeparatorTint(HamsterTheme.colors.primary)
                        }
                    } header: { Text("Suggestions") }
                }
                .padding(.top, -8)
            }
            .background(HamsterTheme.colors.background)
        }
    }
}

#Preview {
    CompletionChooser(
        newItem: .constant("Reis"),
        completions: [CompletionItem.companion.mockCompletionWithCategory],
        categories: [CategoryDefinition.companion.mockDark],
        addItem: { _, _, _ in }
    )
}
