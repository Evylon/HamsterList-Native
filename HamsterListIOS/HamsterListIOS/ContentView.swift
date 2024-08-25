import HamsterListCore
import SwiftUI

struct ContentView: View {
    @State private var listId = ""
    @State private var username = ShoppingListRepositoryCompanion.shared.instance.username

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                TextField("Enter username", text: $username)
                    .disableAutocorrection(true)
                    .textFieldStyle(BackgroundContrastStyle())
                    .padding(.horizontal, 32)
                TextField(
                    "Enter list name",
                    text: $listId,
                    onEditingChanged: { isEditing in
                        if !isEditing {
                            ShoppingListRepositoryCompanion.shared.instance.username = username
                        }
                    }
                )
                    .disableAutocorrection(true)
                    .textFieldStyle(BackgroundContrastStyle())
                    .padding(.horizontal, 32)
                NavigationLink("Load", destination: ShoppingListPage(listId: listId))
                    .padding(.top, 8)
                    .disabled(listId.isEmpty)
            }
        }
    }
}

struct ContentViewPreview: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
