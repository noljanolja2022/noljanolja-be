package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.model.UpsertBannerRequest
import com.noljanolja.server.admin.service.BannerService
import com.noljanolja.server.admin.service.GoogleStorageService
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
class BannerHandler(
    private val bannerService: BannerService,
    private val objectMapper: ObjectMapper,
    private val googleStorageService: GoogleStorageService
) {
    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_PAGE_SIZE = 10
    }


    suspend fun getBanners(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_PAGE_SIZE
        val name = request.queryParamOrNull("name")
        val isActive = request.queryParamOrNull("isActive")?.toBooleanStrictOrNull()

        val (banners, pagination) = bannerService.getBanners(
            page = page,
            pageSize = pageSize,
            name = name,
            isActive = isActive,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = banners,
                    pagination = pagination
                ),
            )
    }

    suspend fun getBanner(request: ServerRequest): ServerResponse {
        val bannerId = request.pathVariable("bannerId").toLongOrNull() ?: throw InvalidParamsException("bannerId")
        val banner = bannerService.getBannerDetail(bannerId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = banner,
                )
            )
    }

    suspend fun upsertBanner(request: ServerRequest): ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val thumbnailFile = reqData.getFieldPartFile("image")
        val payload = reqData.toObject<UpsertBannerRequest>(objectMapper)
        if (thumbnailFile != null) {
            val newImage = googleStorageService.uploadFilePart(thumbnailFile, GoogleStorageService.GIFT_BUCKET)
            payload.image = newImage.path
        }
        val banner = bannerService.upsertBanner(
            payload = payload,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = banner,
                )
            )
    }


    suspend fun deleteBanner(request: ServerRequest): ServerResponse {
        val bannerId = request.pathVariable("bannerId").toLongOrNull() ?: throw InvalidParamsException("bannerId")
        bannerService.deleteBanner(bannerId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}