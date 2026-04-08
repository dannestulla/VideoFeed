package br.gohan.videofeed.domain.feed

import br.gohan.videofeed.domain.model.Video

data class FeedResult(
    val videos: List<Video>,
    val page: Int,
    val hasMore: Boolean
)
