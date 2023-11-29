package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.consumer.adapter.core.toCategory
import com.noljanolja.server.consumer.adapter.core.toGift
import com.noljanolja.server.consumer.adapter.core.toGiftBrand
import com.noljanolja.server.consumer.adapter.gift.GiftApi
import com.noljanolja.server.consumer.model.Gift
import com.noljanolja.server.consumer.model.PurchasedGift
import org.springframework.stereotype.Component

@Component
class GiftService(
    private val giftApi: GiftApi,
) {
    suspend fun getMyGifts(
        userId: String,
        brandId: String?,
        page: Int,
        pageSize: Int,
    ): Pair<List<PurchasedGift>, Pagination> {
        return giftApi.getUserGifts(
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
        return giftApi.buyGift(
            userId = userId,
            giftId = giftId,
        ).toPurchasedGift()
    }

    suspend fun getGifts(
        userId: String,
        brandId: String?,
        page: Int,
        pageSize: Int,
        categoryId: Long?,
        query: String?,
        isFeatured: Boolean?,
        isTodayOffer: Boolean?,
        isRecommended: Boolean?
    ): Pair<List<Gift>, Pagination> {
        return giftApi.getAllGifts(
            userId = userId,
            brandId = brandId,
            page = page,
            pageSize = pageSize,
            categoryId = categoryId,
            query = query,
            isFeatured = isFeatured,
            isTodayOffer = isTodayOffer,
            isRecommended = isRecommended
        ).let { (gifts, pagination) -> Pair(gifts.map { it.toGift() }, pagination) }
    }

    suspend fun getGiftDetail(
        userId: String,
        giftId: String,
    ): Gift {
        return giftApi.getGiftDetail(
            userId = userId,
            giftId = giftId,
        ).toGift()
    }

    suspend fun getCategories(
        page: Int,
        pageSize: Int,
        query: String? = null
    ) = giftApi.getCategories(
        page = page,
        pageSize = pageSize,
        query = query
    ).let { (categories, pagination) -> Pair(categories.map { it.toCategory() }, pagination) }

    suspend fun getBrands(
        page: Int,
        pageSize: Int,
    ) = giftApi.getBrands(
        page = page,
        pageSize = pageSize,
    ).let { (brands, pagination) -> Pair(brands.map { it.toGiftBrand() }, pagination) }
}