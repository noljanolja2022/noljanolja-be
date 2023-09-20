package com.noljanolja.server.gift.service

import com.noljanolja.server.coin_exchange.service.CoinExchangeService
import com.noljanolja.server.common.utils.REASON_PURCHASE_GIFT
import com.noljanolja.server.gift.exception.Error
import com.noljanolja.server.gift.model.Gift
import com.noljanolja.server.gift.repo.*
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
@Transactional
class GiftService(
    private val giftBrandRepo: GiftBrandRepo,
    private val giftCategoryRepo: GiftCategoryRepo,
    private val giftCodeRepo: GiftCodeRepo,
    private val giftRepo: GiftRepo,
    private val coinExchangeService: CoinExchangeService,
) {
    suspend fun buyGift(
        userId: String,
        giftId: Long,
    ): Gift {
        val gift = giftRepo.findByIdForUpdate(giftId) ?: throw Error.GiftNotFound
        if (gift.remaining <= 0) throw Error.NotEnoughGift
        if (
            giftCodeRepo.countByUserIdAndGiftId(
                userId = userId,
                giftId = giftId,
            ) >= gift.maxBuyTimes
        ) throw Error.MaximumBuyTimesReached
        coinExchangeService.addTransaction(
            userId = userId,
            amount = -gift.price.toDouble(),
            reason = REASON_PURCHASE_GIFT
        )
        val savedGift = giftRepo.save(
            gift.apply {
                remaining--
            }
        )
        val giftCode = giftCodeRepo.findFirstByGiftIdAndUserIdIsNull(giftId)!!.let {
            giftCodeRepo.save(
                it.apply {
                    this.userId = userId
                }
            )
        }
        return savedGift.apply {
            codes = listOf(giftCode.code)
            category = giftCategoryRepo.findById(categoryId)!!
            brand = giftBrandRepo.findById(brandId)!!
        }.toGift()
    }

    suspend fun getUserGifts(
        userId: String,
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): Pair<List<Gift>, Long> {
        val gifts = giftRepo.findGiftsOfUser(
            userId = userId,
            categoryId = categoryId,
            brandId = brandId,
            offset = (page - 1) * pageSize,
            limit = pageSize,
        ).toList()
        val categories = giftCategoryRepo.findAllById(gifts.map { it.categoryId }.toMutableSet()).toList()
        val brands = giftBrandRepo.findAllById(gifts.map { it.brandId }.toMutableSet()).toList()
        return Pair(
            gifts.map { gift ->
                gift.category = categories.first { it.id == gift.categoryId }
                gift.brand = brands.first { it.id == gift.brandId }
                gift.codes = giftCodeRepo.findAllByGiftIdAndUserId(
                    giftId = gift.id,
                    userId = userId,
                ).toList().map { it.code }
                gift.toGift()
            },
            giftRepo.countGiftsOfUser(
                userId = userId,
                categoryId = categoryId,
                brandId = brandId,
            )
        )
    }

    suspend fun getAllGifts(
        userId: String?,
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): Pair<List<Gift>, Long> {
        val gifts = giftRepo.findAllBy(
            categoryId = categoryId,
            brandId = brandId,
            limit = pageSize,
            offset = (page - 1) * pageSize,
        ).toList()
        val categories = giftCategoryRepo.findAllById(gifts.map { it.categoryId }.toMutableSet()).toList()
        val brands = giftBrandRepo.findAllById(gifts.map { it.brandId }.toMutableSet()).toList()
        return Pair(
            gifts.map { gift ->
                gift.category = categories.first { it.id == gift.categoryId }
                gift.brand = brands.first { it.id == gift.brandId }
                gift.codes = giftCodeRepo.findAllByGiftIdAndUserId(
                    giftId = gift.id,
                    userId = userId,
                ).toList().map { it.code }
                gift.toGift()
            },
            giftRepo.countAllBy(
                categoryId = categoryId,
                brandId = brandId,
            )
        )
    }

    suspend fun getGiftDetail(
        giftId: Long,
        userId: String?,
    ): Gift {
        return giftRepo.findById(giftId)?.apply {
            category = giftCategoryRepo.findById(categoryId)!!
            brand = giftBrandRepo.findById(brandId)!!
            codes = giftCodeRepo.findAllByGiftIdAndUserId(
                giftId = giftId,
                userId = userId,
            ).toList().map { it.code }
        }?.toGift() ?: throw Error.GiftNotFound
    }

    suspend fun createGift(
        codes: List<String>,
        name: String,
        description: String,
        image: String,
        startTime: Instant,
        endTime: Instant,
        categoryId: Long,
        price: Long,
        brandId: Long,
        maxBuyTimes: Int,
    ): Gift {
        val category = giftCategoryRepo.findById(categoryId) ?: throw Error.CategoryNotFound
        val brand = giftBrandRepo.findById(brandId) ?: throw Error.BrandNotFound
        val gift = giftRepo.save(
            GiftModel(
                name = name,
                description = description,
                image = image,
                startTime = startTime,
                endTime = endTime,
                categoryId = categoryId,
                brandId = brandId,
                total = codes.size,
                remaining = codes.size,
                price = price,
                maxBuyTimes = maxBuyTimes,
            )
        )
        val giftCodes = giftCodeRepo.saveAll(
            codes.map {
                GiftCodeModel(
                    code = it,
                    giftId = gift.id,
                )
            }
        ).toList()
        return gift.apply {
            this.brand = brand
            this.category = category
            this.codes = giftCodes.map { it.code }
        }.toGift()
    }

    suspend fun updateGift(
        giftId: Long,
        name: String?,
        description: String?,
        image: String?,
        price: Long?,
        startTime: Instant?,
        endTime: Instant?,
        maxBuyTimes: Int?,
    ): Gift {
        val gift = giftRepo.findById(giftId) ?: throw Error.GiftNotFound
        val category = giftCategoryRepo.findById(gift.categoryId)!!
        val brand = giftBrandRepo.findById(gift.brandId)!!
        val codes = giftCodeRepo.findAllByGiftId(gift.id).toList().map { it.code }
        return giftRepo.save(
            gift.apply {
                name?.let { this.name = it }
                description?.let { this.description = it }
                image?.let { this.image = it }
                startTime?.let { this.startTime = it }
                endTime?.let { this.endTime = it }
                price?.let { this.price = it }
                maxBuyTimes?.let { this.maxBuyTimes = it }
            }
        ).apply {
            this.brand = brand
            this.category = category
            this.codes = codes
        }.toGift()
    }

    suspend fun deleteGift(
        giftId: Long,
    ) {
        giftRepo.findById(giftId) ?: throw Error.GiftNotFound
        giftRepo.deleteById(giftId)
    }

    suspend fun getCategories(): List<Gift.Category> {
        return giftCategoryRepo.findAll().toList().map { it.toGiftCategory() }
    }

    suspend fun getBrands(
        query: String? = null,
        page: Int,
        pageSize: Int,
    ): Pair<List<Gift.Brand>, Long> {
        val pageable = PageRequest.of(page - 1, pageSize)
        if (query != null) {
            val res =
                giftBrandRepo.findAllByNameContains(
                    query,
                    pageable = pageable
                ).toList().map { it.toGiftBrand() }
            val count = giftBrandRepo.countByNameContains(query)
            return Pair(res, count)
        }
        return Pair(
            giftBrandRepo.findAllBy(
                pageable = pageable
            ).toList().map { it.toGiftBrand() },
            giftBrandRepo.count(),
        )
    }

    suspend fun createBrand(
        name: String,
        image: String,
    ): Gift.Brand {
        return giftBrandRepo.save(
            GiftBrandModel(
                name = name,
                image = image,
            )
        ).toGiftBrand()
    }

    suspend fun updateBrand(
        brandId: Long,
        name: String?,
        image: String?,
    ): Gift.Brand {
        return giftBrandRepo.findById(brandId)?.apply {
            image?.let { this.image = it }
            name?.let { this.name = it }
        }?.let { giftBrandRepo.save(it).toGiftBrand() } ?: throw Error.BrandNotFound
    }

    suspend fun deleteBrand(
        brandId: Long,
    ) {
        giftBrandRepo.findById(brandId) ?: throw Error.BrandNotFound
        giftBrandRepo.deleteById(brandId)
    }
}