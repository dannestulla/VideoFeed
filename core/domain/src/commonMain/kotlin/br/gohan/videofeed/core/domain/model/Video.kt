package br.gohan.videofeed.core.domain.model

data class Video(
    val id: String,
    val title: String,
    val cdnUrl: String,
    val thumbnailUrl: String?,
    val uploaderName: String
)
