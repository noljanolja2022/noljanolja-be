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
        const val BRANDS = "/brands"
        const val BUY = "/send"
    }

    private val extraConfig = serviceConfig.configs.first { it.id == GiftBizServiceConfig.Config.ServiceID.GIFTBIZ }

    private fun constructAuthHeader(apiCode: String): MultiValueMap<String, String> {
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add("custom_auth_code", extraConfig.extra["authCode"].orEmpty())
        formData.add("custom_auth_token", extraConfig.extra["authToken"].orEmpty())
        formData.add("dev_yn", "N")
        formData.add("api_code", apiCode)
        return formData
    }

    suspend fun getGoodsList(
        page: Int = 1,
        pageSize: Int = 20,
    ): GiftBizResponse<GiftBizGoodsListResponse> {
        val formData = constructAuthHeader("0101")
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

    suspend fun getBrandsList(
        page: Int = 1,
        pageSize: Int = 20,
    ) : GiftBizResponse<GiftBizBrandListResponse> {
        val formData = constructAuthHeader("0102")
        formData.add("start", page.toString())
        formData.add("size", pageSize.toString())

        return webClient.post()
            .uri {
                it.path(BRANDS).build()
            }
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .awaitBody<GiftBizResponse<GiftBizBrandListResponse>>()
    }

    suspend fun buyCoupon(
        goodsCode: String,
        phoneNumber: String = "01031475811",
        transactionId: String,
        msgTitle: String = "NolgoBuljia",
        msgDetail: String = "Here is your exchanged coupon"
    ): GiftBizResponse<ShowBizCouponResponse> {
        val formData = constructAuthHeader("0204")
        formData.add("goods_code", goodsCode)
//        formData.add("order_no", )
        formData.add("mms_msg", msgDetail)
        formData.add("mms_title", msgTitle)
        formData.add("callback_no", "01031475811")
        formData.add("phone_no", phoneNumber)
        formData.add("tr_id", transactionId)
//        formData.add("rev_info_yn", )
//        formData.add("rev_info_date", )
//        formData.add("rev_info_time", )
//        formData.add("template_id", )
//        formData.add("banner_id", )
        formData.add("user_id", "sms@ppnyy.com")
        /**
         * Y: receive PIN
         * N: receive SMS
         * I: receive Barcode
         */
        formData.add("gubun", "I")
        return webClient.post()
            .uri {
                it.path(BUY).build()
            }
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .awaitBody<GiftBizResponse<ShowBizCouponResponse>>()
    }
}