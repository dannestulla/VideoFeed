import SwiftUI
import VideoFeed

@main
struct iosAppApp: App {
    init() {
        VideoFeedKt.initKoin(baseUrl: "http://localhost:8081")
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
