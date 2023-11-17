package com.noljanolja.server.core.service

import com.nolgobuljia.server.giftbiz.service.GiftBizApi
import com.noljanolja.server.coin_exchange.service.CoinExchangeService
import com.noljanolja.server.common.exception.CustomBadRequestException
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.common.utils.REASON_PURCHASE_GIFT
import com.noljanolja.server.core.model.dto.PurchasedGift
import com.noljanolja.server.core.model.toGift
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.gift.exception.Error
import com.noljanolja.server.gift.model.Gift
import com.noljanolja.server.gift.model.GiftBrand
import com.noljanolja.server.gift.repo.*
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
@Transactional
class GiftService(
    private val giftBrandRepo: GiftBrandRepo,
    private val userRepo: UserRepo,
    private val giftRepo: GiftRepo,
    private val giftBizApi: GiftBizApi,
    private val coinExchangeService: CoinExchangeService,
    private val giftTransactionRepo: GiftTransactionRepo
) {
    suspend fun importProducts() {
        var pageNum = 1
        val pageSize = 20
        while (true) {
            val res = giftBizApi.getGoodsList(pageNum, pageSize)
            val convertedRes = res.result?.goodsList?.map { it.toGift() } ?: emptyList()
            val brands = convertedRes.map { it.brand }.distinctBy { it.id }
            val existedBrands = giftBrandRepo.findAllById(brands.map { it.id }).toList()
            if (existedBrands.size < brands.size) {
                val existedBrandIds = existedBrands.map { it._id }
                val newBrands = brands.filter { !existedBrandIds.contains(it.id) }
                giftBrandRepo.saveAll(newBrands.map { GiftBrandModel.fromGiftBrand(it) }).toList()
            }
            val payload = convertedRes.map { GiftModel.fromGift(it) }

            giftRepo.saveAll(payload).toList()
            val total = res.result?.listNum ?: 0
            if (total < pageSize * pageNum) {
                break
            }
            pageNum++
        }
    }

    suspend fun buyGift(
        userId: String,
        giftId: String,
    ): PurchasedGift {
        val gift = giftRepo.findById(giftId) ?: throw Error.GiftNotFound
        val brand = giftBrandRepo.findById(gift.brandId) ?: throw Error.GiftNotFound
        val user = userRepo.findById(userId) ?: throw UserNotFound
        coinExchangeService.addTransaction(
            userId = userId,
            amount = -gift.price,
            reason = REASON_PURCHASE_GIFT
        )

        val transactionId = UUID.randomUUID().toString().replace("-", "").substring(0, 20)
        // Temporary used for development. Should switch to ENV dependent soon
        if (true) {
            val fakeTransaction = GiftTransactionModel(
                _id = transactionId,
                userId = userId,
                price = gift.price.toDouble(),
                giftCode = gift._id,
                barCode = "https://imgs.giftishow.co.kr/Resource2/mms/20231115/16/mms_64fc3df5e55ab267c683824b0e4d4222_01.jpg",
                orderNo = "fake Order number",
                pinNumber = "123456"
            ).apply {
                isNewRecord = true
            }
            giftTransactionRepo.save(fakeTransaction)
            return PurchasedGift(
                id = transactionId,
                qrCode = "https://imgs.giftishow.co.kr/Resource2/mms/20231115/16/mms_64fc3df5e55ab267c683824b0e4d4222_01.jpg",
                description = gift.description,
                image = gift.image,
                brand = brand.toGiftBrand(),
                name = gift.name,
                giftId = gift.id
            )
        }
        val res = giftBizApi.buyCoupon(
            goodsCode = gift._id,
            user.phoneNumber,
            transactionId = transactionId
        )
        val couponRes = res.result
        val orderRes = couponRes?.result ?: throw CustomBadRequestException("Unable to create order")
        val newTransaction = GiftTransactionModel(
            _id = transactionId,
            userId = userId,
            price = gift.price.toDouble(),
            giftCode = gift._id,
            barCode = orderRes.couponImgUrl,
            orderNo = orderRes.orderNo,
            pinNumber = orderRes.pinNo
        ).apply {
            isNewRecord = true
        }
        val createdTransaction = giftTransactionRepo.save(newTransaction)
        return PurchasedGift(
            id = createdTransaction.orderNo,
            qrCode = createdTransaction.barCode!!,
            description = gift.description,
            image = gift.image,
            brand = brand.toGiftBrand(),
            name = gift.name,
            giftId = gift.id
        )
    }

    suspend fun getUserGifts(
        userId: String,
        brandId: Long?,
        page: Int,
        pageSize: Int,
    ): Pair<List<PurchasedGift>, Long> {
        val purchasedGifts = giftTransactionRepo.findByUserId(
            userId,
            pageable = Pageable.ofSize(pageSize).withPage(page - 1)
        ).toList()
        val gifts = giftRepo.findAllById(purchasedGifts.map { it.giftCode }.distinct()).toList()
        val brands = giftBrandRepo.findAllById(gifts.map { it.brandId }.toMutableSet()).toList()
        return Pair(
            gifts.map { gift ->
                val brand = brands.first { it._id == gift.brandId }
                val giftTransaction = purchasedGifts.first { it.giftCode == gift._id }
                PurchasedGift.fromGiftModel(gift, brand, giftTransaction)
            },
            giftTransactionRepo.countAllByUserId(
                userId = userId,
            )
        )
    }

    // TODO: implement checking for expired gift
    suspend fun getUserGiftCount(
        userId: String, includeExpired: Boolean
    ): Long {
        return giftTransactionRepo.countAllByUserId(
            userId = userId,
        )
    }

    suspend fun getAllGifts(
        userId: String?,
        brandId: Long?,
        page: Int,
        pageSize: Int,
        forConsumer: Boolean = false
    ): Pair<List<Gift>, Long> {
        val gifts = (if (forConsumer)
            giftRepo.findAllByActive(
                brandId = brandId,
                limit = pageSize,
                offset = (page - 1) * pageSize,
            ) else
            giftRepo.findAllBy(
                brandId = brandId,
                limit = pageSize,
                offset = (page - 1) * pageSize,
            )).toList()
        val brands = giftBrandRepo.findAllById(gifts.map { it.brandId }.toMutableSet()).toList()
        return Pair(
            gifts.map { gift ->
                gift.brand = brands.first { it._id == gift.brandId }
                gift.toGift()
            },
            if (forConsumer)
                giftRepo.countAllByIsActive(true)
            else
                giftRepo.count()
        )
    }

    suspend fun getGiftDetail(
        giftCode: String,
        userId: String?,
    ): Gift {
        return giftRepo.findById(giftCode)?.apply {
            brand = giftBrandRepo.findById(brandId)!!
        }?.toGift() ?: throw Error.GiftNotFound
    }

    suspend fun getBrands(
        query: String? = null,
        page: Int,
        pageSize: Int,
    ): Pair<List<GiftBrand>, Long> {
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
}