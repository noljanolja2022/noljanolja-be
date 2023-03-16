package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MediaRouter(
    private val mediaHandler: MediaHandler,
) {
    companion object {
        const val MEDIA_ROUTES = "/api/v1/media"
    }

    @Bean
    fun mediaRoutes() = coRouter {
        (MEDIA_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            "/sticker-packs".nest {
                GET("", mediaHandler::getStickerPacks)
                GET("/{packId}", mediaHandler::getStickerPack)
                POST("", mediaHandler::createStickerPack)
                DELETE("/{packId}", mediaHandler::deleteStickerPack)
            }
        }
    }
}