package br.gohan.videofeed.presenter.auth

import br.gohan.videofeed.domain.auth.AuthRemoteDataSource
import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.Result

class FakeAuthDataSource : AuthRemoteDataSource {
    var loginResult: Result<String, DataError.Network> = Result.Success("fake-token")
    var registerResult: Result<String, DataError.Network> = Result.Success("fake-token")

    override suspend fun login(email: String, password: String) = loginResult
    override suspend fun register(email: String, password: String) = registerResult
}
