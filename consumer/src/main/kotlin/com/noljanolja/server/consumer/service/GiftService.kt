package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.consumer.adapter.core.*
import com.noljanolja.server.consumer.model.Gift
import com.noljanolja.server.consumer.model.MyGift
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
    ): Pair<List<MyGift>, Pagination> {
        return coreApi.getGifts(
            userId = userId,
            categoryId = categoryId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        ).let { (gifts, pagination) -> Pair(gifts.flatMap { it.toMyGift() }, pagination) }
    }

    suspend fun buyGift(
        userId: String,
        giftId: Long,
    ): MyGift {
        return coreApi.buyGift(
            userId = userId,
            giftId = giftId,
        ).toMyGift().first()
    }

    suspend fun getGifts(
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): Pair<List<Gift>, Pagination> {
        return coreApi.getGifts(
            categoryId = categoryId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
        ).let { (gifts, pagination) -> Pair(gifts.map { it.toGift() }, pagination) }
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
    ).let { (brands, pagination) -> Pair(brands.map { it.toGiftBrand() }, pagination) }
}