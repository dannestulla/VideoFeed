package br.gohan.videofeed.core.data.auth

interface TokenStorage {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
