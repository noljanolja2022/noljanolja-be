package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toGift
import com.noljanolja.server.consumer.adapter.core.toGiftBrand
import com.noljanolja.server.consumer.adapter.core.toGiftCategory
import com.noljanolja.server.consumer.model.Gift
import org.springframework.stereotype.Component

@Component
class GiftService(
    private val coreApi: CoreApi,
) {
    suspend fun getMyGifts(
        userId: String,
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): List<Gift> {
        return coreApi.getGifts(
            userId = userId,
            categoryId = categoryId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        ).map { it.toGift() }
    }

    suspend fun buyGift(
        userId: String,
        giftId: Long,
    ) {
        coreApi.buyGift(
            userId = userId,
            giftId = giftId,
        )
    }

    suspend fun getGifts(
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): List<Gift> {
        return coreApi.getGifts(
            categoryId = categoryId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        ).map { it.toGift(includeCodes = false) }
    }

    suspend fun getGiftDetail(
        userId: String,
        giftId: Long,
    ): Gift {
        return coreApi.getGiftDetail(
            userId = userId,
            giftId = giftId,
        ).toGift()
    }

    suspend fun getGiftCategories() = coreApi.getCategories().map { it.toGiftCategory() }

    suspend fun getBrands(
        page: Int,
        pageSize: Int,
    ) = coreApi.getBrands(
        page = page,
        pageSize = pageSize,
    ).map { it.toGiftBrand() }
}