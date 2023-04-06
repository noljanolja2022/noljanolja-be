package com.noljanolja.server.loyalty.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.loyalty.model.request.UpsertMemberRequest
import com.noljanolja.server.loyalty.service.LoyaltyService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class LoyaltyHandler(
    private val loyaltyService: LoyaltyService
) {
    suspend fun getMemberInfo(request: ServerRequest): ServerResponse {
        val memberId = request.pathVariable("memberId").ifBlank {
            throw DefaultBadRequestException(cause = IllegalArgumentException("Missing memberId"))
        }
        val res = loyaltyService.getMember(memberId)
        return ServerResponse.ok().bodyValueAndAwait(Response(data = res))
    }

    suspend fun updateMemberInfo(request: ServerRequest): ServerResponse {
        val upsertMemberRequest = request.awaitBodyOrNull<UpsertMemberRequest>() ?: throw RequestBodyRequired
        val res = loyaltyService.upsertMember(upsertMemberRequest.memberId)
        return ServerResponse.ok().bodyValueAndAwait(Response(data = res))
    }
}