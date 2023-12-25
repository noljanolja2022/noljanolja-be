package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class LoyaltyRouter(
    private val loyaltyHandler: LoyaltyHandler,
) {
    companion object {
        const val LOYALTY_ROUTE = "/api/v1/loyalty"
    }

    @Bean
    fun loyaltyRoutes() = coRouter {
        (LOYALTY_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            "/me".nest {
                GET("", loyaltyHandler::getMyMemberInfo)
                "/points".nest {
                    GET("", loyaltyHandler::getMyLoyaltyPoints)
                    "{transactionId}".nest {
                        GET("", loyaltyHandler::getLoyaltyPointDetails)
                    }
                }
            }
        }
    }
}