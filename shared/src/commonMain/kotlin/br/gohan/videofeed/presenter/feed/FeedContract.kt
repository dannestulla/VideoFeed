package br.gohan.videofeed.presenter.feed

import br.gohan.videofeed.domain.model.Video

data class VideoUi(
    val id: String,
    val title: String,
    val cdnUrl: String,
    val thumbnailUrl: String?,
    val uploaderName: String
)

fun Video.toVideoUi() = VideoUi(
    id = id, title = title, cdnUrl = cdnUrl,
    thumbnailUrl = thumbnailUrl, uploaderName = uploaderName
)

data class FeedState(
    val videos: List<VideoUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentIndex: Int = 0
)

sealed interface FeedAction {
    data class OnVideoVisible(val index: Int) : FeedAction
    data object OnRefresh : FeedAction
    data object OnUploadClick : FeedAction
    data object OnLoginClick : FeedAction
}

sealed interface FeedEvent {
    data object NavigateToUpload : FeedEvent
    data object NavigateToLogin : FeedEvent
}
