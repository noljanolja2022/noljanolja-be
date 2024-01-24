package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nolgobuljia.server.giftbiz.service.GiftBizApi
import com.noljanolja.server.coin_exchange.service.CoinExchangeService
import com.noljanolja.server.common.exception.CustomBadRequestException
import com.noljanolja.server.common.exception.DefaultInternalErrorException
import com.noljanolja.server.common.exception.ExternalServiceException
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.utils.REASON_PURCHASE_GIFT
import com.noljanolja.server.core.model.Locale
import com.noljanolja.server.core.model.dto.PurchasedGift
import com.noljanolja.server.core.model.toGift
import com.noljanolja.server.gift.exception.Error
import com.noljanolja.server.gift.model.Gift
import com.noljanolja.server.gift.model.GiftBrand
import com.noljanolja.server.gift.model.GiftCategory
import com.noljanolja.server.gift.repo.*
import com.noljanolja.server.gift.rest.IndianGiftReq
import com.noljanolja.server.gift.rest.UpdateGiftCategoryReq
import com.noljanolja.server.gift.rest.UpdateGiftReq
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Component
@Transactional
class GiftService(
    private val giftBrandRepo: GiftBrandRepo,
    private val giftCategoryRepo: GiftCategoryRepo,
    private val giftRepo: GiftRepo,
    private val giftBizApi: GiftBizApi,
    private val coinExchangeService: CoinExchangeService,
    private val giftTransactionRepo: GiftTransactionRepo,
    private val objectMapper: ObjectMapper
) {
    @Value("\${core.env.management}")
    private val env: String = ""

    companion object {
        const val STAGING_REQUEST_TOKEN_URI = "https://sso.sbx.edenred.io/connect/token"
        const val STAGING_CLIENT_ID = "0bd23d3b8bbe4044b5b424b16bbd637d"
        const val STAGING_CLIENT_SECRET = "aReba6hQbHam1MMVkCA1OPkTemoBMdBdBvUE7Jn1"
        const val STAGING_GRANT_TYPE = "client_credentials"
        const val STAGING_SCOPE = "edg-apac-xp-voucher-mgt-api"
        const val STAGING_PRODUCT_ASSETS = "202107148936"
        const val STAGING_X_CLIENT_ID = "6bd1b18274fd46fea88b52eaddf26906"
        const val STAGING_X_CLIENT_SECRET = "5C3ee9FF01254DceA4b8c529c4aCE7D6"
        const val STAGING_X_CORRELATION_ID = "870a411e038541b2b8fbfc10b5793f67"
    }




    //TODO: turn this into a cron job instead of API
    suspend fun importProducts() {
        var pageNum = 1
        val pageSize = 20
        while (true) {
            val res = giftBizApi.getGoodsList(pageNum, pageSize)
            val newGifts = res.result?.goodsList?.map { it.toGift() } ?: emptyList()
            // Sync brands
            val brands = newGifts.map { it.brand }.distinctBy { it.id }
            val existedBrands = giftBrandRepo.findAllById(brands.map { it.id }).toList()
            if (existedBrands.size < brands.size) {
                val existedBrandIds = existedBrands.map { it._id }
                val newBrands = brands.filter { !existedBrandIds.contains(it.id) }
                giftBrandRepo.saveAll(newBrands.map { GiftBrandModel.fromGiftBrand(it, Locale.KOREA.countryCode) }).toList()
            }

            val existedGifts = giftRepo.findAllById(newGifts.map { it.id }).toList()
            val payload = newGifts.map { newGift ->
                val existedGift = existedGifts.firstOrNull { it.id == newGift.id }
                GiftModel.fromGift(existedGift, newGift, Locale.KOREA.countryCode)
            }
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
    ): PurchasedGift? {
        val gift = giftRepo.findById(giftId) ?: throw Error.GiftNotFound
        val locale = gift.locale ?: throw Error.GiftLocaleIsNull
        val brand = giftBrandRepo.findById(gift.brandId) ?: throw Error.BrandNotFound
        val giftLogTransaction = gift.toGiftLogTransaction(brand = brand)

        coinExchangeService.addTransaction(
            userId = userId,
            amount = -gift.price,
            reason = REASON_PURCHASE_GIFT,
            log = objectMapper.writeValueAsString(giftLogTransaction)
        )

        val transactionId = UUID.randomUUID().toString().replace("-", "").substring(0, 20)
        // Temporary used for development. Should switch to ENV dependent soon
        when(env) {
            "dev" -> {
                when(locale) {
                    "KR" -> {
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
                    "IN" -> {
                        val tokenResp = giftBizApi.getRequestToken(
                            clientId = STAGING_CLIENT_ID,
                            clientSecret = STAGING_CLIENT_SECRET,
                            grantType = STAGING_GRANT_TYPE,
                            scope = STAGING_SCOPE,
                            requestTokenURI = STAGING_REQUEST_TOKEN_URI
                        )

                        if (tokenResp.error != null) throw ExternalServiceException(null)

                        val couponRequestURI = "https://xp-voucher-mgt-stg-sg-v1.sg-s1.cloudhub.io/api/catalogs/ETX_001/product_assets/${STAGING_PRODUCT_ASSETS}/products/${giftId}/vouchers/actions/request"
                        val orderNumber = giftBizApi.generateUniqueOrderNumber(userId)
                        val couponResp = giftBizApi.buyIndianCoupon(
                            goodsCode = giftId,
                            userId = userId,
                            transactionId = transactionId,
                            requestToken = tokenResp.access_token ?: throw ExternalServiceException(null),
                            clientId = STAGING_X_CLIENT_ID,
                            clientSecret = STAGING_X_CLIENT_SECRET,
                            correlationId = STAGING_X_CORRELATION_ID,
                            couponRequestURI = couponRequestURI,
                            orderNumber = orderNumber
                        )

                        if (couponResp.meta.status == "failed") throw ExternalServiceException(null)
                        val newTransaction = GiftTransactionModel(
                            _id = transactionId,
                            userId = userId,
                            price = gift.price.toDouble(),
                            giftCode = giftId,
                            orderNo = orderNumber,
                            log = objectMapper.writeValueAsString(couponResp.data)
                        ).apply {
                            isNewRecord = true
                        }

                        val createdTransaction = giftTransactionRepo.save(newTransaction)
                        return PurchasedGift(
                            id = createdTransaction.orderNo,
                            qrCode = createdTransaction.barCode ?: "",
                            description = gift.description,
                            image = gift.image,
                            brand = brand.toGiftBrand(),
                            name = gift.name,
                            giftId = gift.id
                        )
                    }
                    else -> throw DefaultInternalErrorException(null)
                }

            }
            "prod" -> {
                when(locale) {
                    "KR" -> {
                        val res = giftBizApi.buyKoreanCoupon(
                            goodsCode = gift._id,
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
                    "IN" -> {

                    }
                    else -> throw DefaultInternalErrorException(null)
                }
            }
            else -> throw DefaultInternalErrorException(null)
        }

        return null
    }

    suspend fun importIndianGift(payload: IndianGiftReq): Gift {
        val giftBrand = giftBrandRepo.findById(payload.brandId)!!
        val giftCategory = giftCategoryRepo.findById(payload.categoryId)!!

        val existingGift = giftRepo.findById(payload.voucherCode)
        if (existingGift != null) throw Error.GiftExisted
        return createNewGift(
            payload = payload,
            giftBrand = giftBrand,
            giftCategory = giftCategory
        )
    }

    private suspend fun createNewGift(
        payload: IndianGiftReq,
        giftBrand: GiftBrandModel,
        giftCategory: GiftCategoryModel
    ): Gift {
        val defaultEndTime = "2999-12-30 15:00:00"

        val newGift = GiftModel(
            _id = payload.voucherCode,
            giftNo = -1,
            name = payload.name,
            description = payload.description,
            image = payload.image,
            endTime = parseDateTimeToInstant(defaultEndTime),
            brandId = payload.brandId,
            categoryId = payload.categoryId,
            limitDay = -1,
            price = payload.price,
            retailPrice = payload.price,
            isActive = payload.isActive,
            isFeatured = false,
            isTodayOffer = false,
            locale = "IN"
        ).apply {
            isNewRecord = true
        }

        return giftRepo.save(newGift).toGift(giftBrand, giftCategory)
    }

    private fun parseDateTimeToInstant(dateTimeString: String): Instant {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
        return localDateTime.toInstant(ZoneOffset.UTC)
    }

    suspend fun updateGift(giftId: String, payload: UpdateGiftReq): Gift {
        val gift = giftRepo.findById(giftId) ?: throw InvalidParamsException("Id")
        val res = giftRepo.save(gift.apply {
            isActive = payload.isActive
            price = payload.price
            categoryId = payload.categoryId
            isFeatured = payload.isFeatured
            isTodayOffer = payload.isTodayOffer
        })
        return res.toGift()
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
        val categories = giftCategoryRepo.findAllById(gifts.mapNotNull { it.categoryId }.distinct()).toList()
        return Pair(
            purchasedGifts.map { pg ->
                val giftDetail = gifts.first { it.id == pg.giftCode }
                val brand = brands.first { it._id == giftDetail.brandId }
                val category = categories.firstOrNull { it.id == giftDetail.categoryId }
                PurchasedGift.fromGiftModel(giftDetail, brand, category, pg)
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
        query: String?,
        brandId: String?,
        categoryId: Long?,
        page: Int,
        pageSize: Int,
        forConsumer: Boolean = false,
        isFeatured: Boolean? = null,
        isTodayOffer: Boolean? = null,
        isRecommended: Boolean? = null,
        locale: String?
    ): Pair<List<Gift>, Long> {
        var isActiveFilter : Boolean? = null
        if (forConsumer) {
            isActiveFilter = true
        }
        val gifts = giftRepo.findAllBy(
            isActive = isActiveFilter,
            brandId = brandId,
            categoryId = categoryId,
            isFeatured = isFeatured,
            limit = pageSize,
            offset = (page - 1) * pageSize,
            query = query,
            isTodayOffer = isTodayOffer,
            isRecommended = isRecommended,
            locale = locale
        ).toList()

        val brands = giftBrandRepo.findAllById(gifts.map { it.brandId }.toMutableSet()).toList()
        val categories = giftCategoryRepo.findAllById(gifts.mapNotNull { it.categoryId }.distinct()).toList()
        return Pair(
            gifts.map { gift ->
                gift.toGift(
                    brandModel = brands.first { it._id == gift.brandId },
                    categoryModel = categories.firstOrNull { it.id == gift.categoryId }
                )
            },
            giftRepo.countAllBy(
                isActive = isActiveFilter,
                brandId = brandId,
                categoryId = categoryId,
                isFeatured = isFeatured,
                query = query,
                isTodayOffer = isTodayOffer,
                isRecommended = isRecommended,
                locale = locale
            )
        )
    }

    suspend fun getGiftDetail(
        giftCode: String,
        userId: String?,
    ): Gift {
        val gift = giftRepo.findById(giftCode) ?: throw Error.GiftNotFound
        val brand = giftBrandRepo.findById(gift.brandId)!!
        val category = gift.categoryId?.let { giftCategoryRepo.findById(it) }
        return gift.toGift(brand, category)
    }

    suspend fun getBrands(
        query: String? = null,
        page: Int,
        pageSize: Int,
        locale: String? = null
    ): Pair<List<GiftBrand>, Long> {
        val res = giftBrandRepo.findByNameContainsAndLocale(
            query = query,
            locale = locale,
            limit = pageSize,
            offset = (page - 1) * pageSize
        )
            .toList()
            .map { it.toGiftBrand() }

        val count = giftBrandRepo.countByNameContainsAndLocale(
            query = query,
            locale = locale
        )
        return Pair(res, count)
    }

    suspend fun getCategories(
        query: String? = null,
        page: Int,
        pageSize: Int
    ): Pair<List<GiftCategory>, Long> {
        val res = giftCategoryRepo.findByNameContainsAndLocale(
            query = query,
            limit = pageSize,
            offset = (page - 1) * pageSize
        )
            .toList()
            .map { it.toGiftCategory() }

        val count = giftCategoryRepo.countByNameContainsAndLocale(
            query = query
        )
        return Pair(res, count)
    }

    suspend fun updateCategory(id: Long, payload: UpdateGiftCategoryReq) {
        if (id > 0) {
            val existedCategory =
                giftCategoryRepo.findById(id) ?: throw CustomBadRequestException("Invalid category Id provided")
            giftCategoryRepo.save(existedCategory.apply {
                name = payload.name
                updatedAt = Instant.now()
            })
        } else {
            giftCategoryRepo.save(
                GiftCategoryModel(
                    id = id,
                    name = payload.name
                )
            )
        }
    }
}