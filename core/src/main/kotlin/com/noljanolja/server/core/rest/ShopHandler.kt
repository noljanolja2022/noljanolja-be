package com.noljanolja.server.core.rest

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.service.ShopService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class ShopHandler(
    private val shopService: ShopService
) {
    suspend fun getProductList(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: 20
        val query = request.queryParamOrNull("query")
        val showOnlyActive = request.queryParamOrNull("isActive").toBoolean()
        val res = shopService.getProduct(page, pageSize, showOnlyActive, query)
        return ServerResponse.ok().bodyValueAndAwait(
            body = Response(
                data = res.first,
                pagination = Pagination(
                    page = page, pageSize = pageSize, total = res.second
                )
            ),
        )
    }

    suspend fun buyCoupon(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().bodyValueAndAwait(
            body = Response(
                data = "res",
            ),
        )
    }

    suspend fun importProduct(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: 20
        val res = shopService.importProducts(page, pageSize)
        return ServerResponse.ok().bodyValueAndAwait(
            body = Response(
                data = res,
            ),
        )
    }
}