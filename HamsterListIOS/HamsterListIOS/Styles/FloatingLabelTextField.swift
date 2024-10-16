//
//  FloatingLabelTextField.swift
//  HamsterList
//
//  Created by David Hellmers on 16.10.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct FloatingLabelTextField : View {
    @FocusState private var isFocused: Bool
    let label: String
    @Binding var text: String
    var isLabelFloating: Bool {
        isFocused || !text.isEmpty
    }

    var body: some View {
        ZStack {
            Text(label)
                .font(isLabelFloating ? .caption : .body)
                .foregroundStyle(isFocused ? HamsterTheme.colors.primary : .gray)
                .frame(maxWidth: .infinity, alignment: .leading)
                .offset(x: 0, y: isLabelFloating ? -12 : 0)
                .transition(.slide)
                .transition(.scale)
                .animation(.easeIn(duration: 0.1), value: isLabelFloating)
            TextField("", text: $text)
                .focused($isFocused)
                .padding(.top, 12)
        }
        .padding(.vertical, 12)
        .padding(.horizontal, 12)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(UIColor.secondarySystemGroupedBackground))
        )
    }
}

#Preview {
    VStack {
        FloatingLabelTextField(label: "Label", text: .constant(""))
            .padding(12)
        FloatingLabelTextField(label: "Label", text: .constant("Text"))
            .padding(12)
    }
    .background(.gray)
}
