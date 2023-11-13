package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.Gift
import com.noljanolja.server.admin.model.UpdateGiftRequest
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component

@Component
class GiftService(
    private val coreApi: CoreApi
) {

    suspend fun importProductManually() {
        coreApi.importProducts()
    }

    suspend fun updateGift(giftId: Long, data: UpdateGiftRequest) {

    }

    suspend fun getGifts(
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): Response<List<Gift>> {
        return coreApi.getGifts(brandId, page, pageSize)
    }

    suspend fun getGiftDetail(giftId: String): Gift {
        return coreApi.getGift(giftId)
    }


    suspend fun getBrands(page: Int, pageSize: Int, query : String? = null): List<Gift.Brand> {
        return coreApi.getBrands(page, pageSize, query)
    }
}