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

    let uiState: CompletionChooserState
    let addItem: (_ completion: String, _ category: String?) -> Void

    // TODO the List takes up too much space if few items are in it. Maybe use UIKit Stackview Instead :/
    var body: some View {
        if (!uiState.filteredCompletions.isEmpty) {
            VStack {
                Divider()
                    .overlay(Color.gray)
                List {
                    Section {
                        ForEach(uiState.filteredCompletions) { completionState in
                            HStack {
                                Button {
                                    print("Change Completion Category") // TODO
                                } label: {
                                    CategoryCircle(
                                        uiState: completionState.categoryState
                                    )
                                }
                                Button {
                                    addItem(completionState.completion.name,
                                            completionState.completion.category)
                                } label: {
                                    HStack {
                                        Text(completionState.completion.name)
                                        Spacer()
                                    }
                                        .contentShape(Rectangle())
                                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
                                }
                            }
                            .buttonStyle(.plain)
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
        uiState: CompletionChooserState.companion.mock,
        addItem: { _, _ in }
    )
}
