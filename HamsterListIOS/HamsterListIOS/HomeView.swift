import HamsterListCore
import SwiftUI

struct HomeView: View {
    let homeViewModel: HomeViewModel

    @State private var listId = ""
    @State private var serverHostName = ""
    @State private var username = ""

    init() {
        self.homeViewModel = KoinViewModelHelper().homeViewModel
        _listId = State(initialValue: homeViewModel.uiState.currentListId ?? "")
        _serverHostName = State(initialValue: homeViewModel.uiState.serverHostName ?? "")
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
                TextField("Enter server host name", text: $serverHostName)
                    .disableAutocorrection(true)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.URL)
                    .textFieldStyle(BackgroundContrastStyle())
                    .padding(.horizontal, 32)
                NavigationLink(
                    "Load",
                    destination: ShoppingListPage(listId: listId)
                        .onAppear { homeViewModel.updateSettings(newName: username, listId: listId, serverHostName: serverHostName) }
                )
                    .padding(.top, 8)
                    .disabled(listId.isEmpty || username.isEmpty || serverHostName.isEmpty)
            }
        }
    }
}

struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
