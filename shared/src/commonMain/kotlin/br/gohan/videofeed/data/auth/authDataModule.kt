package br.gohan.videofeed.data.auth

import br.gohan.videofeed.domain.auth.AuthRemoteDataSource
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single { KtorAuthDataSource(get(), getProperty("baseUrl"), get()) } bind AuthRemoteDataSource::class
}
