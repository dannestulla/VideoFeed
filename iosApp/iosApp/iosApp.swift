import SwiftUI
import Shared

@main
struct iosAppApp: App {
    init() {
        initKoin(baseUrl: "http://localhost:8081")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
