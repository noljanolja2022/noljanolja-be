package com.noljanolja.server.admin.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MediaRouter(
    private val mediaHandler: MediaHandler
) {
    companion object {
        const val MEDIA_ROUTE = "/api/v1/media"
    }

    @Bean
    fun mediaRoutes() = coRouter {
        (MEDIA_ROUTE and accept(MediaType.MULTIPART_FORM_DATA)).nest {
            "/sticker-packs".nest {
                GET("", mediaHandler::getStickerPacks)
                GET("/{id}/{stickerName}", mediaHandler::getSticker)
                POST("", mediaHandler::createStickerPack)
                DELETE("/{id}", mediaHandler::deleteStickerPack)
            }
            "/videos".nest {
                GET("", mediaHandler::getVideo)
                POST("", mediaHandler::createVideo)
                GET("/promoted", mediaHandler::getPromotedVideos)
                GET("/analytics", mediaHandler::getVideoAnalytics)
                GET("/{id}", mediaHandler::getVideoDetail)
                DELETE("/{id}", mediaHandler::deleteVideo)
                POST("/{id}/promoted", mediaHandler::updatePromotedVideo)
                POST("/{id}/comments", mediaHandler::generateComments)
            }
        }
    }
}