import Combine
import KMPNativeCoroutinesCombine
import HamsterListCore
import SwiftUI

struct HomeView: View {
    let homeViewModel: HomeViewModel

    @State private var navigationPath = NavigationPath()
    @State private var listId = ""
    @State private var serverHostName = ""
    
    private var areInputsValid: Bool {
        !listId.isEmpty && !(uiState.value.username ?? "").isEmpty && !serverHostName.isEmpty
    }

    @ObservedObject
    private var uiState: FlowPublisher<HomeUiState, Error>

    private let appVersion = if let bundleDict = Bundle.main.infoDictionary,
                                let shortVersion = bundleDict["CFBundleShortVersionString"] as? String,
                                let bundleVersion = bundleDict["CFBundleVersion"] as? String
    {
        "\(shortVersion) (\(bundleVersion))"
    } else {
        ""
    }
    
    private var usernameBinding: Binding<String> {
        Binding(
            get: { uiState.value.username ?? "" },
            set: { onAction(HomeActionUpdateUsername(username: $0))}
        )
    }
    
    private var autoLoadLastBinding: Binding<Bool> {
        Binding(
            get: { uiState.value.autoLoadLast },
            set: { onAction(HomeActionUpdateAutoLoadLast(autoLoadLast: $0))}
        )
    }


    init() {
        self.homeViewModel = KoinViewModelHelper().homeViewModel
        self.uiState = FlowPublisher(
            publisher: createPublisher(for: self.homeViewModel.uiStateFlow),
            initial: HomeUiState.companion.initial
        )
        uiState.subscribePublisher()
    }

    private func onAction(_ action: HomeAction) {
        homeViewModel.handleHomeAction(action: action)
    }
    
    var body: some View {
        NavigationStack(path: $navigationPath) {
            VStack(spacing: 20) {
                Spacer()
                FloatingLabelTextField(label: "Username", text: usernameBinding)
                    .disableAutocorrection(true)
                FloatingLabelTextField(label: "List name", text: $listId)
                    .disableAutocorrection(true)
                FloatingLabelTextField(label: "Server host name", text: $serverHostName)
                    .disableAutocorrection(true)
                    .textInputAutocapitalization(.never)
                    .keyboardType(.URL)
                Toggle(isOn: autoLoadLastBinding) {
                    Text("Automatically open last list")
                }
                .padding(.horizontal, 4)
                Button(action: {
                    let hamsterList = HamsterList(listId: listId, serverHostName: serverHostName, title: nil, isLocal: false)
                    onAction(HomeActionLoadHamsterlist(selectedList: hamsterList,
                                                       navigateToList: { navigationPath.append(hamsterList) }))
                }) {
                    Text("Load")
                }
                .disabled(!areInputsValid)
                Spacer()
                Text(appVersion)
                    .font(.caption)
                    .frame(alignment: .bottom)
            }
            .padding(.horizontal, 32)
            .navigationDestination(for: HamsterList.self) { hamsterList in
                ShoppingListPage(hamsterList: hamsterList)
            }
            .frame(maxHeight: .infinity)
            .background(Color(UIColor.systemGroupedBackground))
        }
        .onAppear {
            if (uiState.value.autoLoadLast && !listId.isEmpty) {
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
