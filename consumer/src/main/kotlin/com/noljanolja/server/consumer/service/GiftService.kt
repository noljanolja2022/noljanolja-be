package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toGift
import com.noljanolja.server.consumer.adapter.core.toGiftBrand
import com.noljanolja.server.consumer.model.Gift
import com.noljanolja.server.consumer.model.PurchasedGift
import org.springframework.stereotype.Component

@Component
class GiftService(
    private val coreApi: CoreApi,
) {
    suspend fun getMyGifts(
        userId: String,
        brandId: String?,
        page: Int,
        pageSize: Int,
    ): Pair<List<PurchasedGift>, Pagination> {
        return coreApi.getUserGifts(
            userId = userId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        ).let { (gifts, pagination) -> Pair(gifts.map { it.toPurchasedGift() }, pagination) }
    }

    suspend fun buyGift(
        userId: String,
        giftId: String,
    ): PurchasedGift {
        return coreApi.buyGift(
            userId = userId,
            giftId = giftId,
        ).toPurchasedGift()
    }

    suspend fun getGifts(
        userId: String,
        brandId: String?,
        page: Int,
        pageSize: Int,
    ): Pair<List<Gift>, Pagination> {
        return coreApi.getAllGifts(
            userId = userId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        ).let { (gifts, pagination) -> Pair(gifts.map { it.toGift() }, pagination) }
    }

    suspend fun getGiftDetail(
        userId: String,
        giftId: String,
    ): Gift {
        return coreApi.getGiftDetail(
            userId = userId,
            giftId = giftId,
        ).toGift()
    }

    suspend fun getBrands(
        page: Int,
        pageSize: Int,
    ) = coreApi.getBrands(
        page = page,
        pageSize = pageSize,
    ).let { (brands, pagination) -> Pair(brands.map { it.toGiftBrand() }, pagination) }
}