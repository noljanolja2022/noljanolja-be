package com.noljanolja.server.loyalty.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.loyalty.model.Transaction
import com.noljanolja.server.loyalty.model.request.AddTransactionRequest
import com.noljanolja.server.loyalty.model.request.UpsertMemberRequest
import com.noljanolja.server.loyalty.service.LoyaltyService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.time.Instant

@Component
class LoyaltyHandler(
    private val loyaltyService: LoyaltyService,
) {
    suspend fun getMemberInfo(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId").ifBlank { throw InvalidParamsException("memberId") }
        val member = loyaltyService.getMember(memberId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = member,
                )
            )
    }

    suspend fun updateMemberInfo(request: ServerRequest): ServerResponse {
        val upsertMemberRequest = request.awaitBodyOrNull<UpsertMemberRequest>() ?: throw RequestBodyRequired
        val member = loyaltyService.upsertMember(upsertMemberRequest.memberId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = member
                )
            )
    }

    suspend fun addTransaction(request: ServerRequest): ServerResponse {
        val addTransactionRequest = request.awaitBodyOrNull<AddTransactionRequest>() ?: throw RequestBodyRequired
        val memberId = request.pathVariable("memberId").ifBlank { throw InvalidParamsException("memberId") }
        val transaction = loyaltyService.addTransaction(
            memberId = memberId,
            points = addTransactionRequest.point,
            reason = addTransactionRequest.reason,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transaction,
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
                Transaction.Type.valueOf(it)
            } catch (err: Exception) {
                null
            }
        }
        val month = request.queryParamOrNull("month")?.toIntOrNull()?.takeIf { it in 1..12 }
        val year = request.queryParamOrNull("year")?.toIntOrNull()?.takeIf { it > 0 }
        val memberId = request.pathVariable("memberId").ifBlank { throw InvalidParamsException("memberId") }
        val transactions = loyaltyService.getTransactions(
            memberId = memberId,
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

    suspend fun getTransactionDetails(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("memberId")
        val transactionId = request.pathVariable("transactionId").toLongOrNull()
            ?: throw InvalidParamsException("transactionId")
        val reason = request.queryParamOrNull("reason") ?: throw InvalidParamsException("reason")

        val transactionDetails = loyaltyService.getTransactionDetails(
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