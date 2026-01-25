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

    private var cachedServerHostName: String? {
        uiState.value.knownHamsterLists.first?.serverHostName
    }

    private func loadHamsterList(_ hamsterList: HamsterList) {
        onAction(
            HomeActionLoadHamsterlist(
                selectedList: hamsterList,
                navigateToList: {
                    navigationPath.append(
                        hamsterList
                    )
                }
            )
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
            homeContent
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
                            ListCreationSheet(
                                loadHamsterList: loadHamsterList(_:),
                                initialServerHostName: cachedServerHostName
                            )
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

    private var homeContent: some View {
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
                knownHamsterLists(hamsterLists: uiState.value.knownHamsterLists)
            }
            Spacer()
            Button(action: { onAction(HomeActionOpenListCreationSheet()) }) {
                Text("Add Hamsterlist")
            }
            .disabled(uiState.value.username?.isEmpty ?? true)
            Text(appVersion)
                .font(.caption)
                .frame(alignment: .bottom)
        }
    }

    private func knownHamsterLists(hamsterLists: [HamsterList]) -> some View {
        List {
            Section(
                header: Text("Your Hamsterlists"),
                content: {
                    ForEach(hamsterLists) {
                        hamsterList in
                        NavigationLink(
                            destination: ShoppingListPage(
                                hamsterList: hamsterList
                            )
                        ) {
                            Text(hamsterList.titleOrId)
                        }
                        .swipeActions {
                            Button(
                                role: .destructive,
                                action: {
                                    onAction(
                                        HomeActionDeleteHamsterList(
                                            hamsterList: hamsterList
                                        )
                                    )
                                }
                            ) {
                                Image(systemName: "trash")
                            }.tint(Color.red)
                        }
                    }
                }
            )
        }
    }
}

struct HomeViewPreview: PreviewProvider {
    static var previews: some View {
        HomeView()
    }
}
