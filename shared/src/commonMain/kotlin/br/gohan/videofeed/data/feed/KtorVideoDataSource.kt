package br.gohan.videofeed.data.feed

import br.gohan.videofeed.data.feed.dto.FeedResponseDto
import br.gohan.videofeed.data.feed.dto.VideoDtoData
import br.gohan.videofeed.core.network.get
import br.gohan.videofeed.core.error.DataError
import br.gohan.videofeed.core.error.Result
import br.gohan.videofeed.core.error.map
import br.gohan.videofeed.domain.feed.FeedResult
import br.gohan.videofeed.domain.feed.VideoRemoteDataSource
import br.gohan.videofeed.core.model.Video
import io.ktor.client.HttpClient

class KtorVideoDataSource(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : VideoRemoteDataSource {

    override suspend fun getFeed(page: Int, limit: Int): Result<FeedResult, DataError.Network> =
        httpClient.get<FeedResponseDto>(
            route = "/feed",
            baseUrl = baseUrl,
            queryParameters = mapOf("page" to page, "limit" to limit)
        ).map { it.toDomain() }

    private fun FeedResponseDto.toDomain() = FeedResult(
        videos = videos.map { it.toDomain() },
        page = page,
        hasMore = hasMore
    )

    private fun VideoDtoData.toDomain() = Video(
        id = id,
        title = title,
        cdnUrl = cdnUrl,
        thumbnailUrl = thumbnailUrl,
        uploaderName = uploaderName
    )
}
