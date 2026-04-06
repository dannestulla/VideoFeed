package br.gohan.videofeed.service

import br.gohan.videofeed.dto.FeedResponse
import br.gohan.videofeed.dto.PresignResponse
import br.gohan.videofeed.dto.VideoDto

class VideoService(private val thumbnailService: ThumbnailService) {
    fun presign(filename: String): PresignResponse {
        // Implemented in Task 10
        return PresignResponse("", "")
    }

    fun createVideo(videoKey: String, title: String, uploaderId: String): VideoDto {
        // Implemented in Task 10
        return VideoDto("", title, "", null, "")
    }

    fun getFeed(page: Int, limit: Int): FeedResponse {
        // Implemented in Task 10
        return FeedResponse(emptyList(), page, false)
    }

    fun getVideo(id: String): VideoDto? = null
}
