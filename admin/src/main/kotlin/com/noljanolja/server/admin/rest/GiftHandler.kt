package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.model.UpdateGiftRequest
import com.noljanolja.server.admin.service.GiftService
import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.common.rest.Response
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

    suspend fun getGifts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val res = giftService.getGifts(
            brandId = brandId,
            page = page,
            pageSize = pageSize,
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
        val payload = request.awaitBodyOrNull<UpdateGiftRequest>()
//        val res = giftService.updateGift(giftId, payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = "res",
                )
            )
    }

    suspend fun getBrands(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val query = request.queryParamOrNull("query")
        val brands = giftService.getBrands(
            page = page,
            pageSize = pageSize,
            query = query
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = brands,
                )
            )
    }
}