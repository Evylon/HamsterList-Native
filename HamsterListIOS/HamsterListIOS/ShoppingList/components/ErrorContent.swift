//
//  ErrorContent.swift
//  HamsterList
//
//  Created by David Hellmers on 22.02.25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import HamsterListCore

struct ErrorContent : View {
    let throwable: KotlinThrowable
    let refresh: () -> Void

    var body: some View {
        VStack(alignment: .center, spacing: 12) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 60))
            Text(throwable.message ?? "unknown")
                .font(.body)
                .multilineTextAlignment(.center)
            Button("Retry") { refresh() }
                .font(.title3)
        }.padding(12)
    }
}

#Preview {
    ErrorContent(
        throwable: KotlinThrowable(message: "A problem with the network connection occured"),
        refresh: {}
    )
}
