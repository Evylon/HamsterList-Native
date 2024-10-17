import HamsterListCore
import SwiftUI

struct HomeView: View {
    let homeViewModel: HomeViewModel

    @State private var navigationPath = NavigationPath()
    @State private var autoLoadLast = false
    @State private var listId = ""
    @State private var serverHostName = ""
    @State private var username = ""

    private var areInputsValid: Bool {
        !listId.isEmpty && !username.isEmpty && !serverHostName.isEmpty
    }

    init() {
        self.homeViewModel = KoinViewModelHelper().homeViewModel
        _listId = State(initialValue: homeViewModel.uiState.currentListId ?? "")
        _serverHostName = State(initialValue: homeViewModel.uiState.serverHostName ?? "")
        _username = State(initialValue: homeViewModel.uiState.username ?? "")
        _autoLoadLast = State(initialValue: homeViewModel.uiState.autoLoadLast?.boolValue ?? false)
    }

    var body: some View {
        NavigationStack(path: $navigationPath) {
            VStack(spacing: 20) {
                FloatingLabelTextField(label: "Username", text: $username)
                    .disableAutocorrection(true)
                FloatingLabelTextField(label: "List name", text: $listId)
                    .disableAutocorrection(true)
                FloatingLabelTextField(label: "Server host name", text: $serverHostName)
                    .disableAutocorrection(true)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.URL)
                Toggle(isOn: $autoLoadLast) {
                    Text("Automatically open last list")
                }
                .padding(.horizontal, 4)
                Button(action: {
                    homeViewModel.updateSettings(newName: username,
                                                 listId: listId,
                                                 serverHostName: serverHostName,
                                                 autoLoadLast: autoLoadLast)
                    navigationPath.append(listId)
                }) {
                    Text("Load")
                }
                .disabled(!areInputsValid)
            }
            .padding(.horizontal, 32)
            .navigationDestination(for: String.self) { listId in
                ShoppingListPage(listId: listId)
            }
            .frame(maxHeight: .infinity)
            .background(Color(UIColor.systemGroupedBackground))
        }
        .onAppear {
            if (autoLoadLast && !listId.isEmpty) {
                navigationPath.append(listId)
            }
        }
    }
}

struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
