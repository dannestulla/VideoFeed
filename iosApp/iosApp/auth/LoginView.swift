import SwiftUI
import Shared

@Observable
final class LoginViewHost {
    var state: LoginState
    private let vm: LoginViewModel

    init() {
        vm = loginViewModel()
        state = vm.state.value
    }

    func onAction(_ action: LoginAction) { vm.onAction(action: action) }
}

struct LoginView: View {
    @State private var host = LoginViewHost()
    let onSuccess: () -> Void

    var body: some View {
        VStack(spacing: 20) {
            Text("Sign In").font(.largeTitle).bold()

            TextField("Email", text: Binding(
                get: { host.state.email },
                set: { host.onAction(LoginAction.OnEmailChange(email: $0)) }
            ))
            .keyboardType(.emailAddress)
            .autocapitalization(.none)
            .textFieldStyle(.roundedBorder)

            SecureField("Password", text: Binding(
                get: { host.state.password },
                set: { host.onAction(LoginAction.OnPasswordChange(password: $0)) }
            ))
            .textFieldStyle(.roundedBorder)

            if let error = host.state.error {
                Text(error).foregroundColor(.red).font(.caption)
            }

            Button(action: { host.onAction(LoginAction.OnSubmit()) }) {
                if host.state.isLoading {
                    ProgressView().frame(maxWidth: .infinity)
                } else {
                    Text("Sign In").frame(maxWidth: .infinity)
                }
            }
            .buttonStyle(.borderedProminent)
            .disabled(host.state.isLoading)
        }
        .padding()
        .task {
            for await s in host.vm.state {
                host.state = s
            }
        }
        .task {
            for await event in host.vm.events {
                if event is LoginEvent.NavigateToFeed { onSuccess() }
            }
        }
    }
}
