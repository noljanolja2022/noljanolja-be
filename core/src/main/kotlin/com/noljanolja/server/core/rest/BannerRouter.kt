package com.noljanolja.server.core.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class BannerRouter(
    private val bannerHandler: BannerHandler,
) {
    companion object {
        const val BANNER_ROUTE = "/api/v1/banners"
    }

    @Bean
    fun bannerRoutes() = coRouter {
        (BANNER_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("", bannerHandler::getBanners)
            GET("/{bannerId}", bannerHandler::getBanner)
            PUT("", bannerHandler::upsertBanner)
            DELETE("/{bannerId}", bannerHandler::deleteBanner)
        }
    }
}