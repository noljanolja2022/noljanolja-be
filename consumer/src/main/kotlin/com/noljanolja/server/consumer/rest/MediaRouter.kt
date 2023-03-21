package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class MediaRouter(
    private val mediaHandler: MediaHandler,
) {
    companion object {
        const val MEDIA_ROUTE = "/api/v1/media"
    }

    @Bean
    fun mediaRoutes() = coRouter {
        (MEDIA_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            "/sticker-packs".nest {
                GET("", mediaHandler::getAllStickerPacks)
                GET("/{stickerPackId}", mediaHandler::getStickerPack)
                GET("/{stickerPackId}/{stickerFileName}", mediaHandler::getSticker)
            }
        }
    }
}