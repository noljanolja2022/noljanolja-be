package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.*
import org.springframework.stereotype.Component

@Component
class GiftService(
    private val coreApi: CoreApi
) {

    suspend fun importProductManually() {
        coreApi.importProducts()
    }

    suspend fun getGifts(
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): List<Gift> {
        return coreApi.getGifts(categoryId, brandId, page, pageSize)
    }

    suspend fun getGiftDetail(giftId: Long): Gift {
        return coreApi.getGift(giftId)
    }

    suspend fun createGift(
        payload: CreateGiftRequest
    ): Gift {
        return coreApi.createGift(payload)
    }

    suspend fun updateGift(giftId: Long, payload: UpdateGiftRequest): Gift {
        return coreApi.updateGift(giftId, payload)
    }

    suspend fun deleteGift(giftId: Long) {
        coreApi.deleteGift(giftId)
    }

    suspend fun getBrands(page: Int, pageSize: Int, query : String? = null): List<Gift.Brand> {
        return coreApi.getBrands(page, pageSize, query)
    }

    suspend fun createBrand(
        payload: CreateBrandRequest
    ): Gift.Brand {
        return coreApi.createBrand(payload)
    }

    suspend fun updateBrand(
        brandId: Long,
        payload: UpdateBrandRequest
    ): Gift.Brand {
        return coreApi.updateBrand(brandId, payload)
    }

    suspend fun deleteBrand(
        brandId: Long
    ) {
        coreApi.deleteBrand(brandId)
    }

    suspend fun getCategories(): List<Gift.Category> {
        return coreApi.getCategories()
    }
}