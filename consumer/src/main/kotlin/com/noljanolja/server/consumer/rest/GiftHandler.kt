package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.service.GiftService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

@Component
class GiftHandler(
    private val giftService: GiftService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 20
    }

    suspend fun getMyGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val brandId = request.queryParamOrNull("brandId")
        val (gifts, pagination) = giftService.getMyGifts(
            userId = AuthUserHolder.awaitUser().id,
            page = page,
            pageSize = pageSize,
            brandId = brandId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gifts,
                    pagination = pagination,
                ),
            )
    }

    suspend fun getGiftDetail(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId")
        val gift = giftService.getGiftDetail(
            userId = AuthUserHolder.awaitUser().id,
            giftId = giftId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gift,
                )
            )
    }

    suspend fun buyGift(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId")
        val gift = giftService.buyGift(
            userId = AuthUserHolder.awaitUser().id,
            giftId = giftId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gift,
                ),
            )
    }

    suspend fun getGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val brandId = request.queryParamOrNull("brandId")
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val query = request.queryParamOrNull("query")
        val (gifts, pagination) = giftService.getGifts(
            page = page,
            pageSize = pageSize,
            brandId = brandId,
            userId = AuthUserHolder.awaitUser().id,
            categoryId = categoryId,
            query = query
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gifts,
                    pagination = pagination,
                )
            )
    }

    suspend fun getGiftCategories(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val (categories, pagination) = giftService.getCategories(
            page = page,
            pageSize = pageSize,
            query = query
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = categories,
                    pagination = pagination
                )
            )
    }

    suspend fun getGiftBrands(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val (brands, pagination) = giftService.getBrands(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = brands,
                    pagination = pagination,
                ),
            )
    }
}