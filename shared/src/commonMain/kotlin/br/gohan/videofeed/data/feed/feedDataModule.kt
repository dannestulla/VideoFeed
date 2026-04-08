package br.gohan.videofeed.data.feed

import br.gohan.videofeed.domain.feed.VideoRemoteDataSource
import org.koin.dsl.bind
import org.koin.dsl.module

val feedDataModule = module {
    single { KtorVideoDataSource(get(), getProperty("baseUrl")) } bind VideoRemoteDataSource::class
}
