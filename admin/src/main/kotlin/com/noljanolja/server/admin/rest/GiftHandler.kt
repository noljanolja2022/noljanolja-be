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
import com.noljanolja.server.common.utils.toByteBuffer
import com.noljanolja.server.common.utils.toObject
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.util.*

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
        val thumbnailFile = (reqData["image"]?.firstOrNull() as? FilePart)
            ?: throw DefaultBadRequestException(Exception("Invalid image file provided"))
        val createRequest = reqData.toObject<CreateGiftRequest>(objectMapper)
        val fileExtension = thumbnailFile.filename().split(".").last()
        val fileName = UUID.randomUUID()
        val uploadedFile = googleStorageService.uploadFile(
            path = "${GoogleStorageService.GIFT_BUCKET}/$fileName.$fileExtension",
            contentType = "image/$fileExtension",
            content = thumbnailFile.toByteBuffer(),
            isPublicAccessible = true
        )
        createRequest.image = uploadedFile.path
        val res = giftService.createGift(createRequest)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = res,
                )
            )
    }

    suspend fun updateGift(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<UpdateGiftRequest>() ?: throw RequestBodyRequired
        val giftId = request.pathVariable("giftId").toLongOrNull() ?: throw InvalidParamsException("giftId")
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
        val brands = giftService.getBrands(
            page = page,
            pageSize = pageSize,
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
        val fileExtension = thumbnailFile.filename().split(".").last()
        val fileName = UUID.randomUUID()
        val uploadedFile = googleStorageService.uploadFile(
            path = "${GoogleStorageService.BRAND_BUCKET}/$fileName.$fileExtension",
            contentType = "image/$fileExtension",
            content = thumbnailFile.toByteBuffer(),
            isPublicAccessible = true
        )
        val res = giftService.createBrand(req.apply {
            image = uploadedFile.path
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
            val fileExtension = thumbnailFile.filename().split(".").last()
            val fileName = UUID.randomUUID()
            val uploadedFile = googleStorageService.uploadFile(
                path = "${GoogleStorageService.BRAND_BUCKET}/$fileName.$fileExtension",
                contentType = "image/$fileExtension",
                content = thumbnailFile.toByteBuffer(),
                isPublicAccessible = true
            )
            req.image = uploadedFile.path
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