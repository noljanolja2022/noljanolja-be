package com.noljanolja.server.core.scheduler

import com.nolgobuljia.server.giftbiz.service.GiftBizApi
import com.noljanolja.server.core.model.Locale
import com.noljanolja.server.core.model.toGiftBrand
import com.noljanolja.server.gift.repo.GiftBrandModel
import com.noljanolja.server.gift.repo.GiftBrandRepo
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BrandListJob (
    private val giftBizApi: GiftBizApi,
    private val giftBrandRepo: GiftBrandRepo
) {
    private val logger = LoggerFactory.getLogger(BrandListJob::class.java)

    @Scheduled(cron = "0 0 22 * * *")
    fun syncFromKorea() = runBlocking {
        logger.info("Start sync Korea brand list!")

        val res = giftBizApi.getBrandsList()
        val newBrands = res.result?.brandList?.map { it.toGiftBrand() } ?: emptyList()
        val existedBrands = giftBrandRepo.findAll().toList()

        if (newBrands.isEmpty()) return@runBlocking

        if (existedBrands.isEmpty()) {
            giftBrandRepo.saveAll(GiftBrandModel.fromGiftBrandList(newBrands, Locale.KOREA.countryCode))
        }

        val existedBrandMap = existedBrands.associateBy { it.id }

        newBrands.forEach { newBrand ->
            existedBrandMap[newBrand.id] ?.let { existedBrand ->
                //update existed brand
                val updatedBrand = existedBrand.copy(name = newBrand.name, image = newBrand.image, locale = Locale.KOREA.countryCode)
                giftBrandRepo.save(updatedBrand)
            } ?:run {
                //create new brand
                giftBrandRepo.save(GiftBrandModel.fromGiftBrand(newBrand, Locale.KOREA.countryCode))
            }
        }

        logger.info("End sync Korea brand list!")
    }
}