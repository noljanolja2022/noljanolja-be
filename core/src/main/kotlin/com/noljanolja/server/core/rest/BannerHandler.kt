package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.rest.request.UpsertBannerRequest
import com.noljanolja.server.core.service.BannerService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class BannerHandler(
    private val bannerService: BannerService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun upsertBanner(request: ServerRequest): ServerResponse {
        val banner = with(request.awaitBodyOrNull<UpsertBannerRequest>() ?: throw RequestBodyRequired) {
            bannerService.upsertBanner(
                payload = this,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = banner,
                )
            )
    }

    suspend fun getBanners(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val name = request.queryParamOrNull("name")
        val isActive = request.queryParamOrNull("isActive")?.toBooleanStrictOrNull()

        val res = bannerService.getBanners(
            page = page,
            pageSize = pageSize,
            name = name,
            isActive = isActive,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res.first,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = res.second,
                    )
                ),
            )
    }

    suspend fun getBanner(request: ServerRequest): ServerResponse {
        val bannerId = request.pathVariable("bannerId").toLongOrNull() ?: throw InvalidParamsException("bannerId")
        val banner = bannerService.getBannerDetail(bannerId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = banner,
                )
            )
    }

    suspend fun deleteBanner(request: ServerRequest): ServerResponse {
        val bannerId = request.pathVariable("bannerId").toLongOrNull() ?: throw InvalidParamsException("bannerId")
        bannerService.deleteBanner(bannerId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}