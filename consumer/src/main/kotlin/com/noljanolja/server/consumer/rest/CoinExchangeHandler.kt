package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.rest.request.ExchangePointToCoinRequest
import com.noljanolja.server.consumer.service.CoinExchangeService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.Instant

@Component
class CoinExchangeHandler(
    private val coinExchangeService: CoinExchangeService
) {
    suspend fun getUserBalance(request: ServerRequest): ServerResponse {
        val balance = coinExchangeService.getUserBalance(AuthUserHolder.awaitUser().id)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = balance,
                )
            )
    }

    suspend fun getCoinToPointExchangeRate(request: ServerRequest): ServerResponse {
        val res = coinExchangeService.getCoinToPointExchangeRate()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res
                )
            )
    }

    suspend fun exchangePointToCoin(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<ExchangePointToCoinRequest>() ?: throw RequestBodyRequired
        val transaction = coinExchangeService.exchangePointToCoin(
            userId = AuthUserHolder.awaitUser().id,
            points = payload.points
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transaction,
                )
            )
    }

    suspend fun getUserCoinTransactions(request: ServerRequest): ServerResponse {
        val lastOffsetDate = request.queryParamOrNull("lastOffsetDate")?.let {
            try {
                Instant.parse(it)
            } catch (err: Exception) {
                null
            }
        }
        val type = request.queryParamOrNull("type")
        val month = request.queryParamOrNull("month")?.toIntOrNull()?.takeIf { it in 1..12 }
        val year = request.queryParamOrNull("year")?.toIntOrNull()?.takeIf { it > 0 }
        val transactions = coinExchangeService.getUserCoinTransactions(
            userId = AuthUserHolder.awaitUser().id,
            type = type,
            lastOffsetDate = lastOffsetDate,
            year = year,
            month = month,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transactions,
                )
            )
    }
}
