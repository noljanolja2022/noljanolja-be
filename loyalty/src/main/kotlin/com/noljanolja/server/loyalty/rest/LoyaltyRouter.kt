package com.noljanolja.server.loyalty.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class LoyaltyRouter(
    private val loyaltyHandler: LoyaltyHandler
) {
    companion object {
        const val LOYALTY_ROUTES = "/api/v1/loyalty"
    }

    @Bean
    fun loyaltyRoutes() = coRouter {
        (LOYALTY_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/{memberId}", loyaltyHandler::getMemberInfo)
            POST("", loyaltyHandler::updateMemberInfo)
        }
    }
}