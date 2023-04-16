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
                POST("", mediaHandler::createStickerPack)
            }
            "/videos".nest {
                GET("", mediaHandler::getVideo)
                POST("", mediaHandler::createVideo)
                DELETE("/{id}", mediaHandler::deleteVideo)
            }
        }
    }
}