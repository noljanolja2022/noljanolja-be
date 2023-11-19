package com.noljanolja.server.admin.adapter.gift

import com.noljanolja.server.admin.model.CoreServiceError
import com.noljanolja.server.admin.model.Gift
import com.noljanolja.server.admin.model.GiftBrand
import com.noljanolja.server.admin.model.GiftCategory
import com.noljanolja.server.admin.rest.request.UpdateGiftCategoryReq
import com.noljanolja.server.admin.rest.request.UpdateGiftRequest
import com.noljanolja.server.common.rest.Response
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
        const val ROUTE = "/api/v1/gifts"
    }

    suspend fun importProducts() = webClient.get()
        .uri { uriBuilder ->
            uriBuilder.path("${ROUTE}/import")
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
        .awaitBody<Response<Nothing>>()

    suspend fun getGifts(
        query: String?,
        page: Int = 1,
        pageSize: Int = 10,
    ) = webClient.get()
        .uri { builder ->
            builder.path(ROUTE)
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
        .awaitBody<Response<List<Gift>>>()

    suspend fun getGift(
        giftId: String
    ) = webClient.get()
        .uri { builder ->
            builder.path("${ROUTE}/$giftId").build()
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
        .awaitBody<Response<Gift>>().data!!


    suspend fun updateGift(
        giftId: String, payload: UpdateGiftRequest
    )= webClient.post()
        .uri { builder ->
            builder.path("${ROUTE}/$giftId").build()
        }
        .bodyValue(payload)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<Gift>>().data!!

    suspend fun getBrands(
        page: Int, pageSize: Int, query: String?
    ) = webClient.get()
        .uri { builder ->
            builder.path("${ROUTE}/brands")
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
        .awaitBody<Response<List<GiftBrand>>>()

    suspend fun getCategories(
        page: Int, pageSize: Int, query: String?
    ) = webClient.get()
        .uri { builder ->
            builder.path("${ROUTE}/categories")
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
        .awaitBody<Response<List<GiftCategory>>>()

    suspend fun updateCategory(
        payload: UpdateGiftCategoryReq
    ) = webClient.put()
        .uri { builder ->
            builder.path("${ROUTE}/categories/${payload.id}")
                .build()
        }
        .bodyValue(payload)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError) {
            it.bodyToMono<Response<Nothing>>().mapNotNull { response ->
                CoreServiceError.CoreServiceBadRequest(response.message)
            }
        }
        .onStatus(HttpStatusCode::is5xxServerError) {
            Mono.just(CoreServiceError.CoreServiceInternalError)
        }
        .awaitBody<Response<GiftCategory>>().data
}