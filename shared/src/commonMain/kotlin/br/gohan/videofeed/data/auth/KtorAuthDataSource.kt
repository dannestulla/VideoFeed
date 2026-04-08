package br.gohan.videofeed.data.auth

import br.gohan.videofeed.data.auth.dto.AuthResponseDto
import br.gohan.videofeed.data.auth.dto.LoginRequestDto
import br.gohan.videofeed.data.auth.dto.RegisterRequestDto
import br.gohan.videofeed.data.network.post
import br.gohan.videofeed.domain.auth.AuthRemoteDataSource
import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.Result
import br.gohan.videofeed.domain.error.map
import io.ktor.client.HttpClient

class KtorAuthDataSource(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val tokenStorage: TokenStorage
) : AuthRemoteDataSource {

    override suspend fun login(email: String, password: String): Result<String, DataError.Network> {
        val result = httpClient.post<LoginRequestDto, AuthResponseDto>(
            route = "/auth/login",
            baseUrl = baseUrl,
            body = LoginRequestDto(email, password)
        ).map { it.token }
        if (result is Result.Success) tokenStorage.saveToken(result.data)
        return result
    }

    override suspend fun register(email: String, password: String): Result<String, DataError.Network> {
        val result = httpClient.post<RegisterRequestDto, AuthResponseDto>(
            route = "/auth/register",
            baseUrl = baseUrl,
            body = RegisterRequestDto(email, password)
        ).map { it.token }
        if (result is Result.Success) tokenStorage.saveToken(result.data)
        return result
    }
}
