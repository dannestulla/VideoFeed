package br.gohan.videofeed.presenter.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gohan.videofeed.domain.auth.AuthRemoteDataSource
import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.onFailure
import br.gohan.videofeed.domain.error.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authDataSource: AuthRemoteDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegisterEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is RegisterAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is RegisterAction.OnSubmit -> register()
            is RegisterAction.OnNavigateToLogin -> {
                viewModelScope.launch { _events.send(RegisterEvent.NavigateToLogin) }
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authDataSource.register(_state.value.email, _state.value.password)
                .onSuccess {
                    _events.send(RegisterEvent.NavigateToFeed)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toMessage()) }
                }
        }
    }

    private fun DataError.Network.toMessage(): String = when (this) {
        DataError.Network.CONFLICT -> "Email already registered"
        DataError.Network.NO_INTERNET -> "No internet connection"
        DataError.Network.SERVER_ERROR -> "Server error, please try again"
        else -> "Something went wrong"
    }
}
