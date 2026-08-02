import SwiftUI

@main
struct MahjongIOSApp: App {
    @State private var store = AppStore(repository: LocalStore())

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(store)
                .task { await store.load() }
        }
    }
}

