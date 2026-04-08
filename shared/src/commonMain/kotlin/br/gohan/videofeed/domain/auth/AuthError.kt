package br.gohan.videofeed.domain.auth

import br.gohan.videofeed.domain.error.Error

enum class AuthError : Error {
    INVALID_CREDENTIALS,
    EMAIL_ALREADY_EXISTS,
    INVALID_EMAIL,
    PASSWORD_TOO_SHORT
}
