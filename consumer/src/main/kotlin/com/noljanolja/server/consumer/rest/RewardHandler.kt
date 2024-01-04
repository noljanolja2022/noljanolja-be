package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.service.ReferralService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class RewardHandler (
    private val referralService: ReferralService
) {
    suspend fun getReferralConfig(request: ServerRequest): ServerResponse {
        val referralConfig = referralService.getConfig()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = referralConfig
                )
            )
    }
}