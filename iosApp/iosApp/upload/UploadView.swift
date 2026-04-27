import SwiftUI
import PhotosUI
import Shared

@Observable
final class UploadViewHost {
    var state: UploadState
    private let vm: UploadViewModel

    init() {
        vm = uploadViewModel()
        state = vm.state.value
    }

    func onAction(_ action: UploadAction) { vm.onAction(action: action) }
}

struct UploadView: View {
    @State private var host = UploadViewHost()
    @State private var pickerItem: PhotosPickerItem? = nil
    let onSuccess: () -> Void

    private var isLoading: Bool {
        host.state.status is UploadStatus.Presigning
            || host.state.status is UploadStatus.Uploading
            || host.state.status is UploadStatus.Finalizing
    }

    private var canSubmit: Bool {
        !isLoading && host.state.selectedFilename != nil && !host.state.title.isEmpty
    }

    var body: some View {
        VStack(spacing: 24) {
            Text("Upload Video").font(.title).bold()

            PhotosPicker(selection: $pickerItem, matching: .videos, photoLibrary: .shared()) {
                Label(host.state.selectedFilename ?? "Select Video", systemImage: "video.badge.plus")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.bordered)
            .disabled(isLoading)
            .onChange(of: pickerItem) { _, item in
                guard let item else { return }
                Task { await loadVideo(from: item) }
            }

            TextField("Title", text: Binding(
                get: { host.state.title },
                set: { host.onAction(UploadAction.OnTitleChange(title: $0)) }
            ))
            .textFieldStyle(.roundedBorder)
            .disabled(isLoading)

            Group {
                if let uploading = host.state.status as? UploadStatus.Uploading {
                    VStack(spacing: 8) {
                        ProgressView(value: Double(uploading.progress))
                        Text("Uploading… \(Int(uploading.progress * 100))%").font(.caption)
                    }
                } else if host.state.status is UploadStatus.Presigning {
                    HStack { ProgressView(); Text("Preparing upload…") }
                } else if host.state.status is UploadStatus.Finalizing {
                    HStack { ProgressView(); Text("Saving…") }
                } else if host.state.status is UploadStatus.Done {
                    Text("Upload complete!").foregroundColor(.green)
                } else if let error = host.state.status as? UploadStatus.Error {
                    Text(error.message).foregroundColor(.red).font(.caption)
                }
            }

            Button(action: { host.onAction(UploadAction.OnSubmit()) }) {
                Text("Upload").frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .disabled(!canSubmit)

            Spacer()
        }
        .padding()
        .task {
            for await s in host.vm.state {
                host.state = s
            }
        }
        .task {
            for await event in host.vm.events {
                if event is UploadEvent.NavigateToFeed { onSuccess() }
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
        host.onAction(UploadAction.OnFileSelected(
            bytes: kotlinBytes,
            filename: url.lastPathComponent,
            mimeType: "video/mp4"
        ))
    }
}
