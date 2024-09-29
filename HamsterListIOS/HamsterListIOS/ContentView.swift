import HamsterListCore
import SwiftUI

struct ContentView: View {
    @AppStorage(UserDefaultsIdentifier.currentListId.rawValue)
    private var listId = ""
    @AppStorage(UserDefaultsIdentifier.username.rawValue)
    private var username = ""

    let homeViewModel = KoinViewModelHelper().homeViewModel

    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                TextField("Enter username", text: $username)
                    .disableAutocorrection(true)
                    .textFieldStyle(BackgroundContrastStyle())
                    .padding(.horizontal, 32)
                TextField("Enter list name", text: $listId)
                    .disableAutocorrection(true)
                    .textFieldStyle(BackgroundContrastStyle())
                    .padding(.horizontal, 32)
                NavigationLink(
                    "Load",
                    destination: ShoppingListPage(listId: listId)
                        .onAppear { homeViewModel.setUsername(newName: username) }
                )
                    .padding(.top, 8)
                    .disabled(listId.isEmpty || username.isEmpty)
            }
        }
    }
}

struct ContentViewPreview: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
