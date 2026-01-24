//
//  ListCreationSheet.swift
//  HamsterList
//
//  Created by David Hellmers on 25.01.26.
//  Copyright © 2026 orgName. All rights reserved.
//
import HamsterListCore
import SwiftUI

struct ListCreationSheet: View {
    let loadHamsterList: (HamsterList) -> Void

    @State private var title = ""
    @State private var serverHostName = ""
    @State private var useTitleAsId: Bool = false

    init(
        loadHamsterList: @escaping (HamsterList) -> Void,
        initialServerHostName: String?
    ) {
        self.loadHamsterList = loadHamsterList
        self.serverHostName = initialServerHostName ?? ""
    }

    private var areInputsValid: Bool {
        !title.isEmpty && !serverHostName.isEmpty
    }

    var body: some View {
        VStack(spacing: 20) {
            FloatingLabelTextField(label: "List name", text: $title)
                .disableAutocorrection(true)
            FloatingLabelTextField(
                label: "Server host name",
                text: $serverHostName
            )
            .disableAutocorrection(true)
            .textInputAutocapitalization(.never)
            .keyboardType(.URL)
            Toggle(isOn: $useTitleAsId) {
                Text("Use name as identifier")
            }
            Button(action: {
                let hamsterList =
                    if useTitleAsId {
                        HamsterList(
                            listId: title,
                            serverHostName: serverHostName,
                            title: "",
                            isLocal: false
                        )
                    } else {
                        HamsterList(
                            serverHostName: serverHostName,
                            title: title,
                            isLocal: false
                        )
                    }
                loadHamsterList(hamsterList)
            }) {
                Text("Load")
            }
            .disabled(!areInputsValid)
        }
        .padding(.horizontal, 32)
        .frame(maxHeight: .infinity)
        .background(Color(UIColor.systemGroupedBackground))
    }
}

struct ListCreationSheetPreview: PreviewProvider {
    static var previews: some View {
        ListCreationSheet(
            loadHamsterList: { _ in },
            initialServerHostName: "example.com"
        )
    }
}
