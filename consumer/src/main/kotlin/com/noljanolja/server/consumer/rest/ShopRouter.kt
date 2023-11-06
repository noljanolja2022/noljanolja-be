package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ShopRouter(
    private val shopHandler: ShopHandler
) {
    companion object {
        const val PRODUCT_ROUTE = "/api/v1/shop/products"
    }

    @Bean
    fun productRoutes() = coRouter {
        (PRODUCT_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", shopHandler::getProducts)
        }
    }
}