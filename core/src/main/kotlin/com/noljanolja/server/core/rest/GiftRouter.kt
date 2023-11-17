package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

// TODO: move this to gifts module
@Configuration
class GiftRouter(
    private val giftHandler: GiftHandler,
) {
    companion object {
        const val GIFT_ROUTES = "/api/v1/gifts"
    }

    @Bean
    fun giftRoutes() = coRouter {
        (GIFT_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", giftHandler::getAllGifts)
            GET("import", giftHandler::importGifts)
            "/brands".nest {
                GET("", giftHandler::getBrands)
            }
            "/{giftId}".nest {
                GET("", giftHandler::getGiftDetail)
                POST("/buy", giftHandler::buyGift)
            }

            GET("/users/{userId}", giftHandler::getUserGifts)
            GET("/users/{userId}/count", giftHandler::getUserGiftCount)
        }
    }
}