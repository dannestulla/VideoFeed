package br.gohan.videofeed

import br.gohan.videofeed.auth.data.authDataModule
import br.gohan.videofeed.auth.presenter.LoginViewModel
import br.gohan.videofeed.auth.presenter.RegisterViewModel
import br.gohan.videofeed.di.iosCoreModule
import br.gohan.videofeed.feed.data.feedDataModule
import br.gohan.videofeed.feed.presenter.FeedViewModel
import br.gohan.videofeed.upload.data.uploadDataModule
import br.gohan.videofeed.upload.presenter.UploadViewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.module

private val iosViewModelModule = module {
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }
    factory { FeedViewModel(get()) }
    factory { UploadViewModel(get(), get()) }
}

private var koin: Koin? = null

fun initKoin(baseUrl: String) {
    koin = startKoin {
        properties(mapOf("baseUrl" to baseUrl))
        modules(
            iosCoreModule,
            authDataModule,
            feedDataModule,
            uploadDataModule,
            iosViewModelModule
        )
    }.koin
}

object IOSViewModelFactory {
    fun loginViewModel(): LoginViewModel = koin!!.get()
    fun registerViewModel(): RegisterViewModel = koin!!.get()
    fun feedViewModel(): FeedViewModel = koin!!.get()
    fun uploadViewModel(): UploadViewModel = koin!!.get()
}
