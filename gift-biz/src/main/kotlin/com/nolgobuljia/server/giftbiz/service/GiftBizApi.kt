package com.nolgobuljia.server.giftbiz.service

import com.nolgobuljia.server.giftbiz.GiftBizServiceConfig
import com.nolgobuljia.server.giftbiz.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody


@Component
class GiftBizApi(
    @Qualifier("giftBizWebClient")
    private val webClient: WebClient,
    serviceConfig: GiftBizServiceConfig,
) {
    companion object {
        const val GOODS = "/goods"
    }

    private val extraConfig = serviceConfig.configs.first { it.id == GiftBizServiceConfig.Config.ServiceID.GIFTBIZ }

    private fun constructAuthHeader(): MultiValueMap<String, String> {
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add("custom_auth_code", extraConfig.extra["authCode"].orEmpty())
        formData.add("custom_auth_token", extraConfig.extra["authToken"].orEmpty())
        formData.add("dev_yn", "N")
        return formData
    }

    suspend fun getGoodsList(
        page: Int = 1,
        pageSize: Int = 1,
    ): GiftBizResponse<GiftBizGoodsListResponse> {
        val formData = constructAuthHeader()
        formData.add("api_code", "0101")
        formData.add("start", page.toString())
        formData.add("size", pageSize.toString())

        return webClient.post()
            .uri {
                it.path(GOODS).build()
            }
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .awaitBody<GiftBizResponse<GiftBizGoodsListResponse>>()
    }

//    suspend fun send(
//
//    ) {
//        val formData = constructAuthHeader()
//        formData.add("api_code", "0101")
//        formData.add("start", page.toString())
//        formData.add("size", pageSize.toString())
//
//        return webClient.post()
//            .uri {
//                it.path(GOODS).build()
//            }
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .body(BodyInserters.fromFormData(formData))
//            .retrieve()
//            .awaitBody<ShowBizResponse<GoodsListResponse>>()
//    }
}