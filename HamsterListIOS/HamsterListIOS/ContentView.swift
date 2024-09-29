import HamsterListCore
import SwiftUI

struct ContentView: View {
    let homeViewModel: HomeViewModel

    @State private var listId = ""
    @State private var username = ""

    init() {
        self.homeViewModel = KoinViewModelHelper().homeViewModel
        _listId = State(initialValue: homeViewModel.uiState.currentListId ?? "")
        _username = State(initialValue: homeViewModel.uiState.username ?? "")
    }

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
                        .onAppear { homeViewModel.setUsernameAndListId(newName: username, listId: listId) }
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
