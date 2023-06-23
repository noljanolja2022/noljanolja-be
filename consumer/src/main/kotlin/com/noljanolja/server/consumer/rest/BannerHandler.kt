package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.service.BannerService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class BannerHandler(
    private val bannerService: BannerService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getBanners(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val (banners, pagination) = bannerService.getBanners(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = banners,
                    pagination = pagination,
                )
            )
    }
}