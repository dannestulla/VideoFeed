import SwiftUI
import Shared

@main
struct iosAppApp: App {
    init() {
        IOSKoinHelperKt.doInitKoin(baseUrl: "http://localhost:8081")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
