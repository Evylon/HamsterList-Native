//
//  CategoryCircle.swift
//  HamsterList
//
//  Created by David Hellmers on 29.09.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import HamsterListCore

struct CategoryCircle : View {
    let uiState: CategoryCircleState

    var body: some View {
        ZStack(alignment: .center) {
            Circle()
                .size(CGSize(width: 40, height: 40))
                .fill(uiState.categoryColor.toColor())
            if (uiState.categoryTextLight) {
                Text(uiState.category)
                    .foregroundStyle(.white)
            } else {
                Text(uiState.category)
                    .foregroundStyle(.black)
            }
        }.frame(width: 40, height: 40)
    }
}

#Preview {
    CategoryCircle(uiState: ItemState.companion.mockItemLight.categoryCircleState)
}
