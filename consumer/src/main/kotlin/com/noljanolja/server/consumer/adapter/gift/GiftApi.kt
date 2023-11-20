package com.noljanolja.server.consumer.adapter.gift

import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.CoreGift
import com.noljanolja.server.consumer.adapter.core.CorePurchasedGift
import com.noljanolja.server.consumer.exception.CoreServiceError
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.*

@Component
class GiftApi(
    @Qualifier("coreWebClient") private val webClient: WebClient,
) {
    companion object {
        const val ENDPOINT = "/api/v1/gifts"
    }

    suspend fun getGiftDetail(
        giftId: String,
        userId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$ENDPOINT/{giftId}")
                .queryParam("userId", userId)
                .build(giftId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CoreGift>>().data!!

    suspend fun getAllGifts(
        userId: String? = null,
        brandId: String?,
        page: Int,
        pageSize: Int,
        categoryId: Long?
    ) = webClient.get()
        .uri { builder ->
            builder.path(ENDPOINT)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParam("forConsumer", true)
                .queryParamIfPresent("userId", Optional.ofNullable(userId))
                .queryParamIfPresent("brandId", Optional.ofNullable(brandId))
                .queryParamIfPresent("categoryId", Optional.ofNullable(categoryId))
                .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreGift>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getUserGifts(
        userId: String,
        brandId: String?,
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$ENDPOINT/users/{userId}")
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .queryParamIfPresent("brandId", Optional.ofNullable(brandId))
                .build(userId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CorePurchasedGift>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getUserGiftCount(
        userId: String,
    ) = webClient.get()
        .uri { builder ->
            builder.path("$ENDPOINT/users/{userId}/count")
                .build(userId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<Int>>().data!!

    suspend fun buyGift(
        userId: String,
        giftId: String,
    ) = webClient.post()
        .uri { builder ->
            builder.path("$ENDPOINT/{giftId}/buy")
                .queryParam("userId", userId)
                .build(giftId)
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<CorePurchasedGift>>().data!!

    suspend fun getCategories (
        page: Int,
        pageSize: Int,
        query: String? = null
    ) = webClient.get()
        .uri { builder -> builder.path("$ENDPOINT/categories")
            .queryParam("page", page)
            .queryParam("pageSize", pageSize)
            .queryParamIfPresent("query", Optional.ofNullable(query))
            .build()
        }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreGift.Category>>>().let {
            Pair(it.data!!, it.pagination!!)
        }

    suspend fun getBrands(
        page: Int,
        pageSize: Int,
    ) = webClient.get()
        .uri { builder -> builder.path("$ENDPOINT/brands").build() }
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<List<CoreGift.Brand>>>().let {
            Pair(it.data!!, it.pagination!!)
        }
}