package com.noljanolja.server.gift.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

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
            "/categories".nest {
                GET("", giftHandler::getCategories)
            }

            "/brands".nest {
                GET("", giftHandler::getBrands)
                POST("", giftHandler::createBrand)
                PATCH("{brandId}", giftHandler::updateBrand)
                DELETE("{brandId}", giftHandler::deleteBrand)
            }

            "/{giftId}".nest {
                GET("", giftHandler::getGiftDetail)
                DELETE("", giftHandler::deleteGift)
                PATCH("", giftHandler::updateGift)
                POST("/buy", giftHandler::buyGift)
            }

            GET("", giftHandler::getAllGifts)
            GET("/users/{userId}", giftHandler::getUserGifts)
            POST("", giftHandler::createGift)
        }
    }
}