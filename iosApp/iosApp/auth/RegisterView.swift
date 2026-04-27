import SwiftUI
import Shared

@Observable
final class RegisterViewHost {
    var state: RegisterState
    private let vm: RegisterViewModel

    init() {
        vm = registerViewModel()
        state = vm.state.value
    }

    func onAction(_ action: RegisterAction) { vm.onAction(action: action) }
}

struct RegisterView: View {
    @State private var host = RegisterViewHost()
    let onSuccess: () -> Void

    var body: some View {
        VStack(spacing: 20) {
            Text("Create Account").font(.largeTitle).bold()

            TextField("Email", text: Binding(
                get: { host.state.email },
                set: { host.onAction(RegisterAction.OnEmailChange(email: $0)) }
            ))
            .keyboardType(.emailAddress)
            .autocapitalization(.none)
            .textFieldStyle(.roundedBorder)

            SecureField("Password", text: Binding(
                get: { host.state.password },
                set: { host.onAction(RegisterAction.OnPasswordChange(password: $0)) }
            ))
            .textFieldStyle(.roundedBorder)

            if let error = host.state.error {
                Text(error).foregroundColor(.red).font(.caption)
            }

            Button(action: { host.onAction(RegisterAction.OnSubmit()) }) {
                if host.state.isLoading {
                    ProgressView().frame(maxWidth: .infinity)
                } else {
                    Text("Create Account").frame(maxWidth: .infinity)
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
                if event is RegisterEvent.NavigateToFeed { onSuccess() }
            }
        }
    }
}
