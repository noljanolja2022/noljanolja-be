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
        const val CHANNEL_ROUTE = "/api/v1/media/channels"
    }

    @Bean
    fun videoRoutes() = coRouter {
        (VIDEO_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", videoHandler::upsertVideo)
            GET("", videoHandler::getVideos)
            GET("watching", videoHandler::getWatchingVideos)
            GET("trending", videoHandler::getTrendingVideos)
            GET("promoted", videoHandler::getPromotedVideos)

            "{videoId}".nest {
                DELETE("", videoHandler::deleteVideo)
                GET("", videoHandler::getVideoDetails)
                POST("views", videoHandler::viewVideo)
                POST("likes", videoHandler::likeVideo)
                GET("comments", videoHandler::getVideoComments)
                POST("comments", videoHandler::postComment)
                POST("promote", videoHandler::promoteVideo)
                POST("react-promote", videoHandler::reactToPromotedVideo)
            }
        }
        (CHANNEL_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            "{channelId}".nest {
                GET("", videoHandler::getChannelDetail)
                POST("subscribe", videoHandler::subscribeToChannel)
            }
        }
    }
}