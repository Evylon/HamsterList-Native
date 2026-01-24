import Combine
import HamsterListCore
import KMPNativeCoroutinesCombine
import SwiftUI

struct HomeView: View {
    let homeViewModel: HomeViewModel

    @State private var navigationPath = NavigationPath()

    @ObservedObject
    private var uiState: FlowPublisher<HomeUiState, Error>

    private let appVersion =
        if let bundleDict = Bundle.main.infoDictionary,
            let shortVersion = bundleDict["CFBundleShortVersionString"]
                as? String,
            let bundleVersion = bundleDict["CFBundleVersion"] as? String
        {
            "\(shortVersion) (\(bundleVersion))"
        } else {
            ""
        }

    private var usernameBinding: Binding<String> {
        Binding(
            get: { uiState.value.username ?? "" },
            set: { onAction(HomeActionUpdateUsername(username: $0)) }
        )
    }

    private var autoLoadLastBinding: Binding<Bool> {
        Binding(
            get: { uiState.value.autoLoadLast },
            set: { onAction(HomeActionUpdateAutoLoadLast(autoLoadLast: $0)) }
        )
    }

    private var autoLoadHamsterList: HamsterList?

    private var sheetState: Binding<HomeSheetState?> {
        Binding(
            get: { uiState.value.sheetState },
            set: { value in onAction(HomeActionDismissSheet()) }
        )
    }

    init() {
        let koinHelper = KoinViewModelHelper()
        self.homeViewModel = koinHelper.homeViewModel
        self.uiState = FlowPublisher(
            publisher: createPublisher(for: self.homeViewModel.uiStateFlow),
            initial: HomeUiState.companion.initial
        )
        uiState.subscribePublisher()

        let knownLists = koinHelper.settingsRepository.getKnownLists()
        if let lastLoadedList = knownLists.first {
            autoLoadHamsterList = lastLoadedList
        }
    }

    private func onAction(_ action: HomeAction) {
        homeViewModel.handleHomeAction(action: action)
    }

    var body: some View {
        NavigationStack(path: $navigationPath) {
            VStack(spacing: 20) {
                Spacer()
                Image("logo")
                    .resizable()
                    .frame(width: 140, height: 140)
                FloatingLabelTextField(label: "Username", text: usernameBinding)
                    .disableAutocorrection(true)
                Toggle(isOn: autoLoadLastBinding) {
                    Text("Automatically open last list")
                }
                .padding(.horizontal, 4)
                if uiState.value.knownHamsterLists.isEmpty {
                    Text("No Hamsterlists found.")
                } else {
                    List {
                        Section(
                            header: Text("Your Hamsterlists"),
                            content: {
                                ForEach(uiState.value.knownHamsterLists) {
                                    hamsterList in
                                    NavigationLink(
                                        destination: ShoppingListPage(
                                            hamsterList: hamsterList
                                        )
                                    ) {
                                        Text(hamsterList.titleOrId)
                                    }
                                    .swipeActions {
                                        Button(action: {
                                            onAction(
                                                HomeActionDeleteHamsterList(
                                                    hamsterList: hamsterList
                                                )
                                            )
                                        }) {
                                            Image(systemName: "trash")
                                        }.tint(Color.red)
                                    }
                                }
                            }
                        )
                    }
                }
                Spacer()
                Button(action: { onAction(HomeActionOpenListCreationSheet()) })
                {
                    Text("Add Hamsterlist")
                }
                .disabled(uiState.value.username?.isEmpty ?? true)
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
            .sheet(
                isPresented: .constant(uiState.value.sheetState != nil),
                onDismiss: { onAction(HomeActionDismissSheet()) },
                content: {
                    switch uiState.value.sheetState {
                    case is HomeSheetStateListCreation:
                        listCreationSheet
                    case is HomeSheetStateContentSharing:
                        EmptyView()
                    default:
                        EmptyView()
                    }
                }
            )
        }
        .onAppear {
            if let hamsterList = autoLoadHamsterList {
                navigationPath.append(hamsterList)
            }
        }
    }

    @State private var title = ""
    @State private var serverHostName = ""
    @State private var useTitleAsId: Bool = false

    private var areInputsValid: Bool {
        !title.isEmpty && !serverHostName.isEmpty
    }

    var listCreationSheet: some View {
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
                onAction(
                    HomeActionLoadHamsterlist(
                        selectedList: hamsterList,
                        navigateToList: { navigationPath.append(hamsterList) }
                    )
                )
            }) {
                Text("Load")
            }
            .disabled(!areInputsValid)
        }
        .padding(.horizontal, 32)
        .frame(maxHeight: .infinity)
        .background(Color(UIColor.systemGroupedBackground))
        .onAppear {
            let lastLoadedList = uiState.value.knownHamsterLists.first
            serverHostName = lastLoadedList?.serverHostName ?? ""
        }
    }
}

struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
