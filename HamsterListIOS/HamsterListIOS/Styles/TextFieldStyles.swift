//
//  Styles.swift
//  HamsterListIOS
//
//  Created by David Hellmers on 11.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct BackgroundContrastStyle : TextFieldStyle {
    public func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding(12)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color(UIColor.secondarySystemGroupedBackground))
            )
            .textFieldStyle(ImprovedFocusStyle())
    }
}

struct ImprovedFocusStyle : TextFieldStyle {
    @FocusState private var isFocused: Bool
    public func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .focused($isFocused)
            .onTapGesture {
                isFocused = true
            }
    }
}

struct BackgroundContrastStylePreview: PreviewProvider {
    static var previews: some View {
        @State var text = ""
        VStack {
            TextField("placeholder", text: $text)
                .textFieldStyle(BackgroundContrastStyle())
                .padding(16)
            TextField("placeholder", text: $text)
                .textFieldStyle(ImprovedFocusStyle())
                .padding(16)
        }
    }
}
