package com.noljanolja.server.loyalty.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.loyalty.model.request.AddTransactionRequest
import com.noljanolja.server.loyalty.model.request.UpsertMemberRequest
import com.noljanolja.server.loyalty.service.LoyaltyService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class LoyaltyHandler(
    private val loyaltyService: LoyaltyService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

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
            point = addTransactionRequest.point,
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
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val memberId = request.pathVariable("memberId").ifBlank { throw InvalidParamsException("memberId") }
        val (transactions, total) = loyaltyService.getTransactions(
            memberId = memberId,
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transactions,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }
}