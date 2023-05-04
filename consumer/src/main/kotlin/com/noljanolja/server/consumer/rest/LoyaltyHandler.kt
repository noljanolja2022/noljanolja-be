package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.service.LoyaltyService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class LoyaltyHandler(
    private val loyaltyService: LoyaltyService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

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
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val (transactions, pagination) = loyaltyService.getLoyaltyPoints(
            userId = AuthUserHolder.awaitUser().id,
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = transactions,
                    pagination = pagination,
                )
            )
    }
}