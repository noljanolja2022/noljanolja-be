package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class GiftRouter(
    private val giftHandler: GiftHandler
) {
    companion object {
        const val GIFT_ROUTES = "/api/v1/gifts"
    }

    @Bean
    fun giftRoutes() = coRouter {
        (GIFT_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", giftHandler::getGifts)
            GET("/me", giftHandler::getMyGifts)
            GET("/brands", giftHandler::getGiftBrands)
            GET("/categories", giftHandler::getGiftCategories)
            GET("/{giftId}", giftHandler::getGiftDetail)
            POST("/{giftId}/buy", giftHandler::buyGift)
        }
    }
}