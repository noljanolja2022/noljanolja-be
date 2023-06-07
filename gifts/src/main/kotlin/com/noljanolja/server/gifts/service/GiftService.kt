package com.noljanolja.server.gifts.service

import com.noljanolja.server.gifts.exception.Error
import com.noljanolja.server.gifts.model.Gift
import com.noljanolja.server.gifts.repo.*
import com.noljanolja.server.loyalty.service.LoyaltyService
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
    private val loyaltyService: LoyaltyService,
) {
    suspend fun buyGift(
        userId: String,
        giftId: Long,
    ) {
        val gift = giftRepo.findByIdForUpdate(giftId) ?: throw Error.GiftNotFound
        if (gift.remaining <= 0) throw Error.NotEnoughGift
        loyaltyService.addTransaction(
            memberId = userId,
            point = -gift.price,
            reason = "Buy gift"
        )
        giftRepo.save(
            gift.apply {
                remaining--
            }
        )
        giftCodeRepo.findByGiftIdAndUserIdIsNull(giftId)!!.let {
            giftCodeRepo.save(
                it.apply {
                    this.userId = userId
                }
            )
        }
    }

    suspend fun getGifts(
        userId: String?,
        categoryId: Long?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): List<Gift> {
        return (if (userId.isNullOrBlank()) {
            giftRepo.findAllBy(
                categoryId = categoryId,
                brandId = brandId,
                limit = pageSize,
                offset = (page - 1) * pageSize,
            )
        } else {
            giftRepo.findGiftsOfUser(
                userId = userId,
                categoryId = categoryId,
                brandId = brandId,
                offset = (page - 1) * pageSize,
                limit = pageSize,
            )
        }).toList().let { gifts ->
            val categories = giftCategoryRepo.findAllById(gifts.map { it.categoryId }.toMutableSet()).toList()
            val brands = giftBrandRepo.findAllById(gifts.map { it.brandId }.toMutableSet()).toList()
            gifts.map { gift ->
                gift.category = categories.first { it.id == gift.categoryId }
                gift.brand = brands.first { it.id == gift.brandId }
                gift.codes = giftCodeRepo.findAllByGiftIdAndUserId(
                    giftId = gift.id,
                    userId = userId,
                ).toList().map { it.code }
                gift.toGift()
            }
        }
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
        page: Int,
        pageSize: Int,
    ): List<Gift.Brand> {
        return giftBrandRepo.findAllBy(
            pageable = PageRequest.of(page - 1, pageSize)
        ).toList().map { it.toGiftBrand() }
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