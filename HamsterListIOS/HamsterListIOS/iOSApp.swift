import SwiftUI
import HamsterListCore

@main
struct iOSApp: App {
    init() {
        KoinModule_iosKt.doInitKoin()
    }

    var body: some Scene {
		WindowGroup {
			HomeView()
		}
	}
}
