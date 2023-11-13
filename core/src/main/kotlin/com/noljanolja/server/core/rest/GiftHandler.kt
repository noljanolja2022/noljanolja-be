package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.service.GiftService
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

    suspend fun importGifts(request: ServerRequest): ServerResponse {
        giftService.importProducts()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getAllGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val forConsumer = request.queryParamOrNull("forConsumer")?.toBoolean() ?: false
        val userId = request.queryParamOrNull("userId")
        val (gifts, total) = giftService.getAllGifts(
            brandId = brandId,
            page = page,
            pageSize = pageSize,
            userId = userId,
            forConsumer = forConsumer,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gifts,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun getUserGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val userId = request.pathVariable("userId").takeIf { it.isNotBlank() } ?: throw InvalidParamsException("userId")
//        val query = request.queryParamOrNull("query")
        val (gifts, total) = giftService.getUserGifts(
            brandId = brandId,
            page = page,
            pageSize = pageSize,
            userId = userId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gifts,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun getGiftDetail(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId")
        val userId = request.queryParamOrNull("userId")
        val gift = giftService.getGiftDetail(
            giftCode = giftId,
            userId = userId,
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
        val userId = request.queryParamOrNull("userId")?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")
        val gift = giftService.buyGift(
            userId = userId,
            giftId = giftId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gift,
                )
            )
    }

    suspend fun getBrands(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val (brands, total) = giftService.getBrands(
            page = page,
            pageSize = pageSize,
            query = query
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = brands,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }
}