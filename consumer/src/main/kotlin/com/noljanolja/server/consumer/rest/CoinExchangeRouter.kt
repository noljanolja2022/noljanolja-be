package com.noljanolja.server.consumer.rest

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class CoinExchangeRouter(
    private val coinExchangeHandler: CoinExchangeHandler,
) {
    companion object {
        const val COIN_EXCHANGE_ROUTE = "/api/v1/coin-exchange"
    }

    @Bean
    fun coinExchangeRoutes() = coRouter {
        (COIN_EXCHANGE_ROUTE and accept(MediaType.APPLICATION_JSON)).nest {
            GET("/rate", coinExchangeHandler::getCoinToPointExchangeRate)

            "/me".nest {
                GET("/transactions", coinExchangeHandler::getUserCoinTransactions)
                GET("/balance", coinExchangeHandler::getUserBalance)
                POST("/convert", coinExchangeHandler::exchangePointToCoin)
            }
        }
    }
}