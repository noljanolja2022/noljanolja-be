package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ShopRouter(
    private val shopHandler: ShopHandler,
) {
    companion object {
        const val PRODUCT_ROUTE = "/api/v1/shop/product"
        const val COUPON_ROUTE = "/api/v1/shop/coupon"
    }

    @Bean
    fun shopRoutes() = coRouter {
        (PRODUCT_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", shopHandler::getProductList)
            GET("import", shopHandler::importProduct)
        }
        (COUPON_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            POST("", shopHandler::buyCoupon)
        }
    }
}