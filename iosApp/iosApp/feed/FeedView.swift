import SwiftUI
import AVFoundation
import Shared

@Observable
final class FeedViewHost {
    var state: FeedState
    let vm: FeedViewModel

    init() {
        vm = IOSKoinHelperKt.feedViewModel()
        state = FeedState()
    }
}

struct FeedView: View {
    @State private var host = FeedViewHost()
    @State private var currentIndex = 0
    @State private var players: [String: AVPlayer] = [:]
    private let preloader = VideoPreloader()

    let onNavigateToLogin: () -> Void
    let onNavigateToUpload: () -> Void

    var body: some View {
        GeometryReader { geo in
            ZStack(alignment: .topTrailing) {
                if host.state.videos.isEmpty && host.state.isLoading {
                    ProgressView().frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    TabView(selection: $currentIndex) {
                        ForEach(Array(host.state.videos.enumerated()), id: \.element.id) { index, video in
                            VideoItemView(
                                video: video,
                                player: player(for: video),
                                isVisible: index == currentIndex,
                                size: geo.size
                            )
                            .tag(index)
                        }
                    }
                    .tabViewStyle(.page(indexDisplayMode: .never))
                    .ignoresSafeArea()
                    .onChange(of: currentIndex) { _, newIndex in
                        host.vm.onAction(action: FeedActionOnVideoVisible(index: Int32(newIndex)))
                        prefetchNext(from: newIndex)
                    }
                }

                Button(action: onNavigateToUpload) {
                    Image(systemName: "plus.circle.fill").font(.title).padding()
                }
                .foregroundColor(.white)
            }
        }
        .onAppear {
            host.vm.observeState { s in host.state = s }
            host.vm.observeEvents { event in
                if event is FeedEventNavigateToLogin { onNavigateToLogin() }
                else if event is FeedEventNavigateToUpload { onNavigateToUpload() }
            }
        }
        .onDisappear { host.vm.clear() }
    }

    private func player(for video: VideoUi) -> AVPlayer {
        if let existing = players[video.id] { return existing }
        let url = URL(string: video.cdnUrl)!
        let p: AVPlayer
        if let cached = preloader.asset(for: url) {
            p = AVPlayer(playerItem: AVPlayerItem(asset: cached))
        } else {
            p = AVPlayer(url: url)
        }
        players[video.id] = p
        return p
    }

    private func prefetchNext(from index: Int) {
        let urls = (1...2).compactMap { offset -> URL? in
            let next = index + offset
            guard next < host.state.videos.count else { return nil }
            return URL(string: host.state.videos[next].cdnUrl)
        }
        Task { await preloader.preload(urls: urls) }
    }
}

struct VideoItemView: View {
    let video: VideoUi
    let player: AVPlayer
    let isVisible: Bool
    let size: CGSize

    var body: some View {
        ZStack(alignment: .bottomLeading) {
            VideoPlayerView(player: player)
                .frame(width: size.width, height: size.height)
                .onAppear { if isVisible { player.play() } }
                .onDisappear { player.pause() }
                .onChange(of: isVisible) { _, visible in
                    if visible { player.play() } else { player.pause() }
                }

            VStack(alignment: .leading, spacing: 4) {
                Text(video.title).font(.headline).foregroundColor(.white)
                Text(video.uploaderName).font(.subheadline).foregroundColor(.white.opacity(0.8))
            }
            .padding()
            .padding(.bottom, 40)
        }
    }
}
