import SwiftUI
import VideoFeed
import KMPNativeCoroutinesAsync

struct LoginView: View {
    @StateObject private var holder = ViewModelHolder(IOSViewModelFactory.shared.loginViewModel())
    @State private var state = LoginState(email: "", password: "", isLoading: false, error: nil)
    let onSuccess: () -> Void

    private var vm: LoginViewModel { holder.viewModel }

    var body: some View {
        VStack(spacing: 20) {
            Text("Sign In").font(.largeTitle).bold()

            TextField("Email", text: Binding(
                get: { state.email },
                set: { vm.onAction(action: LoginActionOnEmailChange(email: $0)) }
            ))
            .keyboardType(.emailAddress)
            .autocapitalization(.none)
            .textFieldStyle(.roundedBorder)

            SecureField("Password", text: Binding(
                get: { state.password },
                set: { vm.onAction(action: LoginActionOnPasswordChange(password: $0)) }
            ))
            .textFieldStyle(.roundedBorder)

            if let error = state.error {
                Text(error).foregroundColor(.red).font(.caption)
            }

            Button(action: { vm.onAction(action: LoginActionOnSubmit()) }) {
                if state.isLoading {
                    ProgressView().frame(maxWidth: .infinity)
                } else {
                    Text("Sign In").frame(maxWidth: .infinity)
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
                if event is LoginEventNavigateToFeed { onSuccess() }
            }
        }
    }
}
