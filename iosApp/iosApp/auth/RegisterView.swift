import SwiftUI
import VideoFeed
import KMPNativeCoroutinesAsync

struct RegisterView: View {
    @StateObject private var holder = ViewModelHolder(IOSViewModelFactory.shared.registerViewModel())
    @State private var state = RegisterState(email: "", password: "", isLoading: false, error: nil)
    let onSuccess: () -> Void

    private var vm: RegisterViewModel { holder.viewModel }

    var body: some View {
        VStack(spacing: 20) {
            Text("Create Account").font(.largeTitle).bold()

            TextField("Email", text: Binding(
                get: { state.email },
                set: { vm.onAction(action: RegisterActionOnEmailChange(email: $0)) }
            ))
            .keyboardType(.emailAddress)
            .autocapitalization(.none)
            .textFieldStyle(.roundedBorder)

            SecureField("Password", text: Binding(
                get: { state.password },
                set: { vm.onAction(action: RegisterActionOnPasswordChange(password: $0)) }
            ))
            .textFieldStyle(.roundedBorder)

            if let error = state.error {
                Text(error).foregroundColor(.red).font(.caption)
            }

            Button(action: { vm.onAction(action: RegisterActionOnSubmit()) }) {
                if state.isLoading {
                    ProgressView().frame(maxWidth: .infinity)
                } else {
                    Text("Create Account").frame(maxWidth: .infinity)
                }
            }
            .buttonStyle(.borderedProminent)
            .disabled(state.isLoading)
        }
        .padding()
        .task {
            for await newState in asyncSequence(for: vm.stateNative) {
                state = newState
            }
        }
        .task {
            for await event in asyncSequence(for: vm.eventsNative) {
                if event is RegisterEventNavigateToFeed { onSuccess() }
            }
        }
    }
}
