package com.noljanolja.server.consumer.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.service.ShopService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class ShopHandler(
    private val shopService: ShopService,
    private val objectMapper: ObjectMapper
) {
    suspend fun getProducts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: 20
        val query = request.queryParamOrNull("query")
        val res = shopService.getProducts(page, pageSize, query)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(
                    data = res.data,
                    pagination = res.pagination
                )
            )
    }
}