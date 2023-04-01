package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class VideoRouter(
    private val videoHandler: VideoHandler,
) {
    companion object {
        const val VIDEO_ROUTE = "/api/v1/media/videos"
    }

    @Bean
    fun videoRoutes() = coRouter {
        (VIDEO_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", videoHandler::upsertVideo)
            GET("", videoHandler::getVideos)

            "{videoId}".nest {
                DELETE("", videoHandler::deleteVideo)
                GET("", videoHandler::getVideoDetails)
            }
        }
    }
}