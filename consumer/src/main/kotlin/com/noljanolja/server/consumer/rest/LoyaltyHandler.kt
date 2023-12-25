package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.service.LoyaltyService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import java.time.Instant

@Component
class LoyaltyHandler(
    private val loyaltyService: LoyaltyService,
) {

    suspend fun getMyMemberInfo(request: ServerRequest): ServerResponse {
        val member = loyaltyService.getMemberInfo(AuthUserHolder.awaitUser().id)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = member,
                )
            )
    }

    suspend fun getMyLoyaltyPoints(request: ServerRequest): ServerResponse {
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
        val transactions = loyaltyService.getLoyaltyPoints(
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

    suspend fun getLoyaltyPointDetails(request: ServerRequest): ServerResponse {
        val transactionId = request.pathVariable("transactionId").toLongOrNull()
            ?: throw InvalidParamsException("transactionId")
        val reason = request.queryParamOrNull("reason")
            ?: throw InvalidParamsException("reason")
        val memberId = AuthUserHolder.awaitUser().id

        val transactionDetails = loyaltyService.getLoyaltyPointDetails(
            memberId = memberId,
            transactionId = transactionId,
            reason = reason
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transactionDetails
                )
            )
    }
}