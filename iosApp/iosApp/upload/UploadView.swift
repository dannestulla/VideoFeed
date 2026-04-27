import SwiftUI
import PhotosUI
import VideoFeed
import KMPNativeCoroutinesAsync

struct UploadView: View {
    @StateObject private var holder = ViewModelHolder(IOSViewModelFactory.shared.uploadViewModel())
    @State private var state = UploadState(title: "", selectedFilename: nil, status: UploadStatusIdle())
    @State private var pickerItem: PhotosPickerItem? = nil
    let onSuccess: () -> Void

    private var vm: UploadViewModel { holder.viewModel }

    private var isLoading: Bool {
        state.status is UploadStatusPresigning
            || state.status is UploadStatusUploading
            || state.status is UploadStatusFinalizing
    }

    private var canSubmit: Bool {
        !isLoading && state.selectedFilename != nil && !state.title.isEmpty
    }

    var body: some View {
        VStack(spacing: 24) {
            Text("Upload Video").font(.title).bold()

            PhotosPicker(selection: $pickerItem, matching: .videos, photoLibrary: .shared()) {
                Label(state.selectedFilename ?? "Select Video", systemImage: "video.badge.plus")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.bordered)
            .disabled(isLoading)
            .onChange(of: pickerItem) { _, item in
                guard let item else { return }
                Task { await loadVideo(from: item) }
            }

            TextField("Title", text: Binding(
                get: { state.title },
                set: { vm.onAction(action: UploadActionOnTitleChange(title: $0)) }
            ))
            .textFieldStyle(.roundedBorder)
            .disabled(isLoading)

            Group {
                if let uploading = state.status as? UploadStatusUploading {
                    VStack(spacing: 8) {
                        ProgressView(value: Double(uploading.progress))
                        Text("Uploading… \(Int(uploading.progress * 100))%").font(.caption)
                    }
                } else if state.status is UploadStatusPresigning {
                    HStack { ProgressView(); Text("Preparing upload…") }
                } else if state.status is UploadStatusFinalizing {
                    HStack { ProgressView(); Text("Saving…") }
                } else if state.status is UploadStatusDone {
                    Text("Upload complete!").foregroundColor(.green)
                } else if let error = state.status as? UploadStatusError {
                    Text(error.message).foregroundColor(.red).font(.caption)
                }
            }

            Button(action: { vm.onAction(action: UploadActionOnSubmit()) }) {
                Text("Upload").frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .disabled(!canSubmit)

            Spacer()
        }
        .padding()
        .task {
            for await newState in asyncSequence(for: vm.stateNative) {
                state = newState
            }
        }
        .task {
            for await event in asyncSequence(for: vm.eventsNative) {
                if event is UploadEventNavigateToFeed { onSuccess() }
            }
        }
    }

    private func loadVideo(from item: PhotosPickerItem) async {
        guard let url = try? await item.loadTransferable(type: URL.self),
              let data = try? Data(contentsOf: url) else { return }
        let bytes = [UInt8](data)
        let kotlinBytes = KotlinByteArray(size: Int32(bytes.count))
        for (i, b) in bytes.enumerated() {
            kotlinBytes.set(index: Int32(i), value: Int8(bitPattern: b))
        }
        vm.onAction(action: UploadActionOnFileSelected(
            bytes: kotlinBytes,
            filename: url.lastPathComponent,
            mimeType: "video/mp4"
        ))
    }
}
