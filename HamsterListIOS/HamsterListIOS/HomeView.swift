import HamsterListCore
import SwiftUI

struct HomeView: View {
    let homeViewModel: HomeViewModel

    @State private var navigationPath = NavigationPath()
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
        NavigationStack(path: $navigationPath) {
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
                Button(action: {
                    homeViewModel.updateSettings(newName: username,
                                                 listId: listId,
                                                 serverHostName: serverHostName)
                    navigationPath.append(listId)
                }) {
                    Text("Load")
                }
                .disabled(listId.isEmpty || username.isEmpty || serverHostName.isEmpty)
            }
            .navigationDestination(for: String.self) { listId in
                ShoppingListPage(listId: listId)
            }
        }
    }
}

struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
