package br.gohan.videofeed.domain.feed

import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.Result

interface VideoRemoteDataSource {
    suspend fun getFeed(page: Int, limit: Int): Result<FeedResult, DataError.Network>
}
