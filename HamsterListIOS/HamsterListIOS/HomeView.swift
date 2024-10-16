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
                FloatingLabelTextField(label: "Username", text: $username)
                    .disableAutocorrection(true)
                    .padding(.horizontal, 32)
                FloatingLabelTextField(label: "List name", text: $listId)
                    .disableAutocorrection(true)
                    .padding(.horizontal, 32)
                FloatingLabelTextField(label: "Server host name", text: $serverHostName)
                    .disableAutocorrection(true)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.URL)
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
            .frame(maxHeight: .infinity)
            .background(Color(UIColor.systemGroupedBackground))
        }
    }
}

struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
