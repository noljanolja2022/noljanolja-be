package com.noljanolja.server.gift.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.gift.rest.request.CreateBrandRequest
import com.noljanolja.server.gift.rest.request.CreateGiftRequest
import com.noljanolja.server.gift.rest.request.UpdateBrandRequest
import com.noljanolja.server.gift.rest.request.UpdateGiftRequest
import com.noljanolja.server.gift.service.GiftService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class GiftHandler(
    private val giftService: GiftService,
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getAllGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val userId = request.queryParamOrNull("userId")
        val (gifts, total) = giftService.getAllGifts(
            categoryId = categoryId,
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

    suspend fun getUserGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val userId = request.pathVariable("userId").takeIf { it.isNotBlank() } ?: throw InvalidParamsException("userId")
        val (gifts, total) = giftService.getUserGifts(
            categoryId = categoryId,
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
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
        val userId = request.queryParamOrNull("userId")
        val gift = giftService.getGiftDetail(
            giftId = giftId,
            userId = userId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gift,
                )
            )
    }

    suspend fun createGift(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<CreateGiftRequest>() ?: throw RequestBodyRequired
        val gift = with(payload) {
            giftService.createGift(
                codes = codes,
                name = name,
                description = description,
                image = image,
                startTime = startTime,
                endTime = endTime,
                categoryId = categoryId,
                brandId = brandId,
                price = price,
                maxBuyTimes = maxBuyTimes,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gift,
                )
            )
    }

    suspend fun updateGift(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpdateGiftRequest>() ?: throw RequestBodyRequired
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
        val gift = with(payload) {
            giftService.updateGift(
                giftId = giftId,
                name = name,
                description = description,
                image = image,
                price = price,
                startTime = startTime,
                endTime = endTime,
                maxBuyTimes = maxBuyTimes,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = gift,
                )
            )
    }

    suspend fun deleteGift(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
        giftService.deleteGift(giftId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun buyGift(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
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

    suspend fun getCategories(request: ServerRequest): ServerResponse {
        val categories = giftService.getCategories()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = categories,
                ),
            )
    }

    suspend fun getBrands(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val (brands, total) = giftService.getBrands(
            page = page,
            pageSize = pageSize,
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

    suspend fun createBrand(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<CreateBrandRequest>() ?: throw RequestBodyRequired
        val brand = with(payload) {
            giftService.createBrand(
                name = name,
                image = image,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = brand,
                )
            )
    }

    suspend fun updateBrand(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpdateBrandRequest>() ?: throw RequestBodyRequired
        val brandId = request.pathVariable("brandId").toLongOrNull() ?: throw InvalidParamsException("brandId")
        val brand = with(payload) {
            giftService.updateBrand(
                name = name,
                image = image,
                brandId = brandId,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = brand,
                )
            )
    }

    suspend fun deleteBrand(request: ServerRequest): ServerResponse {
        val brandId = request.pathVariable("brandId").toLongOrNull() ?: throw InvalidParamsException("brandId")
        giftService.deleteBrand(brandId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}