package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.InvalidParamsException
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
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getMyGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val gifts = giftService.getMyGifts(
            userId = AuthUserHolder.awaitUser().id,
            page = page,
            pageSize = pageSize,
            categoryId = categoryId,
            brandId = brandId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gifts,
                ),
            )
    }

    suspend fun getGiftDetail(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
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
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
        giftService.buyGift(
            userId = AuthUserHolder.awaitUser().id,
            giftId = giftId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>(),
            )
    }

    suspend fun getGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val gifts = giftService.getGifts(
            page = page,
            pageSize = pageSize,
            categoryId = categoryId,
            brandId = brandId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gifts,
                )
            )
    }

    suspend fun getGiftCategories(request: ServerRequest): ServerResponse {
        val categories = giftService.getGiftCategories()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = categories
                ),
            )
    }

    suspend fun getGiftBrands(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val brands = giftService.getBrands(
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = brands
                ),
            )
    }
}