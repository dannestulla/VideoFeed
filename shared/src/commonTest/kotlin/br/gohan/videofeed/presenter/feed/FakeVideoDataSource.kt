package br.gohan.videofeed.presenter.feed

import br.gohan.videofeed.domain.error.DataError
import br.gohan.videofeed.domain.error.Result
import br.gohan.videofeed.domain.feed.FeedResult
import br.gohan.videofeed.domain.feed.VideoRemoteDataSource
import br.gohan.videofeed.domain.model.Video

class FakeVideoDataSource : VideoRemoteDataSource {
    var shouldReturnError = false
    var hasMore = false
    var secondPageVideos: List<Video> = emptyList()
    private var callCount = 0

    override suspend fun getFeed(page: Int, limit: Int): Result<FeedResult, DataError.Network> {
        if (shouldReturnError) return Result.Error(DataError.Network.SERVER_ERROR)
        callCount++
        val videos = if (callCount == 1) listOf(makeVideo("1"), makeVideo("2"))
                     else secondPageVideos
        return Result.Success(FeedResult(videos = videos, page = page, hasMore = hasMore && callCount == 1))
    }
}
