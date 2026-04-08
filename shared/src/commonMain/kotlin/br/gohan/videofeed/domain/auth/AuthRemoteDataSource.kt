package br.gohan.videofeed.domain.auth

import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.Result

interface AuthRemoteDataSource {
    suspend fun login(email: String, password: String): Result<String, DataError.Network>
    suspend fun register(email: String, password: String): Result<String, DataError.Network>
}
