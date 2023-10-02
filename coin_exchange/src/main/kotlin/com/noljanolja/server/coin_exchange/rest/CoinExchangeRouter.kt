package com.noljanolja.server.coin_exchange.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CoinExchangeRouter(
    private val coinExchangeHandler: CoinExchangeHandler,
) {
    companion object {
        const val COIN_EXCHANGE_ROUTES = "/api/v1/coin-exchange"
    }

    @Bean
    fun coinExchangeRoutes() = coRouter {
        (COIN_EXCHANGE_ROUTES and accept(MediaType.APPLICATION_JSON)).nest {
            GET("rate", coinExchangeHandler::getCoinToPointExchangeConfig)
            PUT("rate", coinExchangeHandler::updateCoinToPointExchangeConfig)
            "users".nest {
                POST("/{userId}/convert", coinExchangeHandler::exchangePointToCoin)
                GET("/{userId}/balance", coinExchangeHandler::getUserBalance)
                GET("/{userId}/transactions", coinExchangeHandler::getTransactions)
            }
        }
    }
}