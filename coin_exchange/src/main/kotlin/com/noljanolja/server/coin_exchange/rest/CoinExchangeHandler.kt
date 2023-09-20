package com.noljanolja.server.coin_exchange.rest

import com.noljanolja.server.coin_exchange.model.CoinTransaction
import com.noljanolja.server.coin_exchange.model.request.ExchangePointRequest
import com.noljanolja.server.coin_exchange.service.CoinExchangeService
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.Instant

@Component
class CoinExchangeHandler(
    private val coinExchangeService: CoinExchangeService,
) {
    suspend fun getUserBalance(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val userBalance = coinExchangeService.getUserBalance(userId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = userBalance,
                )
            )
    }

    suspend fun exchangePointToCoin(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<ExchangePointRequest>() ?: throw RequestBodyRequired
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val transaction = coinExchangeService.exchangePointToCoin(
            points = payload.points,
            userId = userId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transaction
                )
            )
    }

    suspend fun getCoinToPointExchangeRate(request: ServerRequest): ServerResponse {
        val rate = coinExchangeService.getCoinToPointExchangeRate()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = rate,
                )
            )
    }

    suspend fun getTransactions(request: ServerRequest): ServerResponse {
        val lastOffsetDate = request.queryParamOrNull("lastOffsetDate")?.let {
            try {
                Instant.parse(it)
            } catch (err: Exception) {
                null
            }
        }
        val type = request.queryParamOrNull("type")?.let {
            try {
                CoinTransaction.Type.valueOf(it)
            } catch (err: Exception) {
                null
            }
        }
        val month = request.queryParamOrNull("month")?.toIntOrNull()?.takeIf { it in 1..12 }
        val year = request.queryParamOrNull("year")?.toIntOrNull()?.takeIf { it > 0 }
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val transactions = coinExchangeService.getTransactions(
            userId = userId,
            month = month,
            year = year,
            type = type,
            lastOffsetDate = lastOffsetDate,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transactions,
                )
            )
    }
}