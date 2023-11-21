package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.gift.GiftApi
import com.noljanolja.server.admin.model.Gift
import com.noljanolja.server.admin.model.GiftBrand
import com.noljanolja.server.admin.model.GiftCategory
import com.noljanolja.server.admin.rest.request.UpdateGiftCategoryReq
import com.noljanolja.server.admin.rest.request.UpdateGiftRequest
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component

@Component
class GiftService(
    private val giftApi: GiftApi
) {

    suspend fun importProductManually() {
        giftApi.importProducts()
    }

    suspend fun updateGift(giftId: String, data: UpdateGiftRequest): Gift {
        return giftApi.updateGift(giftId, data)
    }

    suspend fun getGifts(
        query: String?,
        page: Int,
        pageSize: Int,
        isFeatured: String?
    ): Response<List<Gift>> {
        return giftApi.getGifts(query, page, pageSize, isFeatured)
    }

    suspend fun getGiftDetail(giftId: String): Gift {
        return giftApi.getGift(giftId)
    }


    suspend fun getBrands(page: Int, pageSize: Int, query : String? = null): Response<List<GiftBrand>> {
        return giftApi.getBrands(page, pageSize, query)
    }

    suspend fun getCategories(page: Int, pageSize: Int, query : String? = null): Response<List<GiftCategory>> {
        return giftApi.getCategories(page, pageSize, query)
    }

    suspend fun updateCategory(payload: UpdateGiftCategoryReq): GiftCategory? {
        return giftApi.updateCategory(payload)
    }
}