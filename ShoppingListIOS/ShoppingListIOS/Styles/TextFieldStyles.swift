//
//  Styles.swift
//  ShoppingListIOS
//
//  Created by David Hellmers on 11.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct BackgroundContrastStyle : TextFieldStyle {
    public func _body(configuration: TextField<Self._Label>) -> some View {
        configuration
            .padding(4)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .stroke(lineWidth: 0.2)
            )
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color(UIColor.secondarySystemBackground))
            )
    }
}

struct BackgroundContrastStylePreview: PreviewProvider {
    static var previews: some View {
        @State var text = ""
        TextField("placeholder", text: $text)
            .textFieldStyle(BackgroundContrastStyle())
            .padding(16)
    }
}
