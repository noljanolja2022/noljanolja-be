package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class TransferPointRouter(
    private val transferPointHandler: TransferPointHandler
) {
    companion object {
        const val TRANSFER_POINT_ROUTES = "/api/v1/transfer-point"
    }

    @Bean
    fun transferPointRoutes() = coRouter {
        (TRANSFER_POINT_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            POST("request", transferPointHandler::requestPoint)
            POST("send", transferPointHandler::sendPoint)
        }
    }
}