package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.model.CreateBrandRequest
import com.noljanolja.server.admin.model.CreateGiftRequest
import com.noljanolja.server.admin.model.UpdateBrandRequest
import com.noljanolja.server.admin.model.UpdateGiftRequest
import com.noljanolja.server.admin.service.GiftService
import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.common.utils.getFieldPartFile
import com.noljanolja.server.common.utils.toObject
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

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
        val categoryId = request.queryParamOrNull("categoryId")?.toLongOrNull()
        val brandId = request.queryParamOrNull("brandId")?.toLongOrNull()
        val res = giftService.getGifts(
            categoryId = categoryId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun getGiftDetail(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
        val res = giftService.getGiftDetail(giftId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun createGift(request: ServerRequest): ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val thumbnailFile = reqData.getFieldPartFile("image")
            ?: throw DefaultBadRequestException(Exception("Invalid image file provided"))
        val createRequest = reqData.toObject<CreateGiftRequest>(objectMapper)
        val newImage = googleStorageService.uploadFilePart(thumbnailFile, GoogleStorageService.GIFT_BUCKET)
        createRequest.image = newImage.path
        val res = giftService.createGift(createRequest)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun updateGift(request: ServerRequest): ServerResponse {
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val payload = reqData.toObject<UpdateGiftRequest>(objectMapper)
        val thumbnailFile = reqData.getFieldPartFile("image")
        if (thumbnailFile != null) {
            val newImage = googleStorageService.uploadFilePart(thumbnailFile, GoogleStorageService.GIFT_BUCKET)
            payload.image = newImage.path
        }
        val res = giftService.updateGift(giftId, payload)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
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

    suspend fun getCategories(request: ServerRequest): ServerResponse {
        val categories = giftService.getCategories()
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = categories,
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

    suspend fun createBrand(request: ServerRequest): ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val thumbnailFile = reqData.getFieldPartFile("image")
            ?: throw DefaultBadRequestException(Exception("Invalid file input"))
        val req = reqData.toObject<CreateBrandRequest>(objectMapper)
        val newImage = googleStorageService.uploadFilePart(thumbnailFile, GoogleStorageService.BRAND_BUCKET)
        val res = giftService.createBrand(req.apply {
            image = newImage.path
        })
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun updateBrand(request: ServerRequest): ServerResponse {
        val brandId = request.pathVariable("brandId").toLongOrNull() ?: throw InvalidParamsException("brandId")
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val req = reqData.toObject<UpdateBrandRequest>(objectMapper)
        val thumbnailFile = reqData.getFieldPartFile("image")
        if (thumbnailFile != null) {
            val newImage = googleStorageService.uploadFilePart(thumbnailFile, GoogleStorageService.BRAND_BUCKET)
            req.image = newImage.path
        }
        val res = giftService.updateBrand(brandId, req)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
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