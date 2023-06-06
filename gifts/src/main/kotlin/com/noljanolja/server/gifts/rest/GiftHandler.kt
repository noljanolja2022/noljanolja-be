package com.noljanolja.server.gifts.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.gifts.rest.request.CreateGiftRequest
import com.noljanolja.server.gifts.rest.request.UpdateGiftRequest
import com.noljanolja.server.gifts.service.GiftService
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

    suspend fun getGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val userId = request.queryParamOrNull("userId")
        val gifts = giftService.getGifts(
            categoryId = categoryId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
            userId = userId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = gifts,
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
                body = gift
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
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = gift,
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
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = gift,
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
        giftService.buyGift(
            userId = userId,
            giftId = giftId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}