package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.rest.request.IndianGiftRequest
import com.noljanolja.server.admin.rest.request.UpdateGiftCategoryReq
import com.noljanolja.server.admin.rest.request.UpdateGiftRequest
import com.noljanolja.server.admin.service.GiftService
import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class GiftHandler(
    private val giftService: GiftService,
    private val googleStorageService: GoogleStorageService,
    private val objectMapper: ObjectMapper
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun importGifts(request: ServerRequest): ServerResponse {
        giftService.importProductManually()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = "res",
                )
            )
    }

    suspend fun importIndianGift(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<IndianGiftRequest>() ?: throw RequestBodyRequired
        val gift = giftService.importIndianGift(payload)

        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    code = HttpStatus.CREATED.value(),
                    data = gift
                )
            )
    }

    suspend fun getGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val isFeatured = request.queryParamOrNull("isFeatured")?.toBooleanStrictOrNull()
        val isTodayOffer = request.queryParamOrNull("isTodayOffer")?.toBooleanStrictOrNull()
        val isRecommended = request.queryParamOrNull("isRecommended")?.toBooleanStrictOrNull()
        val locale = request.queryParamOrNull("locale");
        val res = giftService.getGifts(
            query = query,
            page = page,
            pageSize = pageSize,
            isFeatured = isFeatured,
            isTodayOffer = isTodayOffer,
            isRecommended = isRecommended,
            locale = locale
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res.data,
                    pagination = res.pagination
                )
            )
    }

    suspend fun getGiftDetail(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId")
        val res = giftService.getGiftDetail(giftId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun updateGift(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId")
        val payload = request.awaitBodyOrNull<UpdateGiftRequest>() ?: throw RequestBodyRequired
        val res = giftService.updateGift(giftId, payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun getBrands(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val locale = request.queryParamOrNull("locale")
        val res = giftService.getBrands(
            page = page,
            pageSize = pageSize,
            query = query,
            locale = locale
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res.data,
                    pagination = res.pagination
                )
            )
    }

    suspend fun getCategories(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val locale = request.queryParamOrNull("locale")
        val res = giftService.getCategories(
            page = page,
            pageSize = pageSize,
            query = query,
            locale = locale
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res.data,
                    pagination = res.pagination
                )
            )
    }

    suspend fun updateCategory(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpdateGiftCategoryReq>() ?: throw RequestBodyRequired
        val res = giftService.updateCategory(payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res
                )
            )
    }
}