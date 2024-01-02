package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.service.GiftService
import com.noljanolja.server.gift.rest.UpdateGiftCategoryReq
import com.noljanolja.server.gift.rest.UpdateGiftReq
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

// TODO: move this to gifts module
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
        val brandId = request.queryParamOrNull("brandId")
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val query = request.queryParamOrNull("query")
        val forConsumer = request.queryParamOrNull("forConsumer")?.toBoolean() ?: false
        val userId = request.queryParamOrNull("userId")
        val isFeatured = request.queryParamOrNull("isFeatured")?.toBooleanStrictOrNull()
        val isTodayOffer = request.queryParamOrNull("isTodayOffer")?.toBooleanStrictOrNull()
        val isRecommended = request.queryParamOrNull("isRecommended")?.toBooleanStrictOrNull()
        val locale = request.queryParamOrNull("locale")
        val (gifts, total) = giftService.getAllGifts(
            brandId = brandId,
            categoryId = categoryId,
            query = query,
            page = page,
            pageSize = pageSize,
            userId = userId,
            forConsumer = forConsumer,
            isFeatured = isFeatured,
            isTodayOffer = isTodayOffer,
            isRecommended = isRecommended,
            locale = locale
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

    suspend fun getUserGiftCount(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId")
        val includeExpired = request.queryParamOrNull("includeExpired")?.toBoolean() ?: false
        val res = giftService.getUserGiftCount(userId, includeExpired)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res
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

    suspend fun updateGift(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId")
        val payload = request.awaitBodyOrNull<UpdateGiftReq>() ?: throw RequestBodyRequired
        val gift = giftService.updateGift(
            giftId,
            payload
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
        val locale = request.queryParamOrNull("locale")
        val (brands, total) = giftService.getBrands(
            page = page,
            pageSize = pageSize,
            query = query,
            locale = locale
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

    suspend fun getCategories(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val (data, total) = giftService.getCategories(
            page = page,
            pageSize = pageSize,
            query = query
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = data,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun updateCategory(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("id").toLong()
        val payload = request.awaitBodyOrNull<UpdateGiftCategoryReq>() ?: throw RequestBodyRequired
        val res = giftService.updateCategory(giftId, payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res,
                )
            )
    }
}