package br.gohan.videofeed.service

import br.gohan.videofeed.config.R2Config
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.util.UUID

class ThumbnailService {
    private val log = LoggerFactory.getLogger(ThumbnailService::class.java)

    fun extractAndUpload(videoKey: String): String {
        val cdnVideoUrl = "${R2Config.publicUrl}/$videoKey"
        val tmpFile = File.createTempFile("thumbnail-${UUID.randomUUID()}", ".jpg")
        return try {
            val process = ProcessBuilder(
                "ffmpeg", "-i", cdnVideoUrl,
                "-ss", "00:00:01",
                "-vframes", "1",
                "-q:v", "2",
                "-y",
                tmpFile.absolutePath
            )
                .redirectErrorStream(true)
                .start()

            val exitCode = process.waitFor()
            if (exitCode != 0) {
                log.warn("FFmpeg exited with code $exitCode for key $videoKey")
                return "thumbnails/placeholder.jpg"
            }

            val thumbnailKey = "thumbnails/${UUID.randomUUID()}.jpg"
            R2Config.client.putObject(
                PutObjectRequest.builder()
                    .bucket(R2Config.bucket)
                    .key(thumbnailKey)
                    .contentType("image/jpeg")
                    .build(),
                RequestBody.fromFile(tmpFile)
            )
            thumbnailKey
        } catch (e: Exception) {
            log.error("Thumbnail extraction failed for $videoKey", e)
            "thumbnails/placeholder.jpg"
        } finally {
            tmpFile.delete()
        }
    }
}
