package br.gohan.videofeed.di

import br.gohan.videofeed.data.auth.DataStoreTokenStorage
import br.gohan.videofeed.data.auth.TokenStorage
import br.gohan.videofeed.data.auth.authDataModule
import br.gohan.videofeed.data.network.HttpClientFactory
import br.gohan.videofeed.presenter.auth.login.LoginViewModel
import br.gohan.videofeed.presenter.auth.register.RegisterViewModel
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val coreAndroidModule = module {
    single<TokenStorage> { DataStoreTokenStorage(androidContext()) }
    single { HttpClientFactory.create(Android.create(), get()) }
}

val authPresentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}

val appModules = listOf(
    coreAndroidModule,
    authDataModule,
    authPresentationModule
)
