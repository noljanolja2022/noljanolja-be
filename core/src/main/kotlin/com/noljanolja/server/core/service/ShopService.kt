package com.noljanolja.server.core.service

import com.nolgobuljia.server.giftbiz.service.GiftBizApi
import com.noljanolja.server.core.model.Product
import com.noljanolja.server.core.model.toProduct
import com.noljanolja.server.core.repo.shop.ProductModel
import com.noljanolja.server.core.repo.shop.ProductRepo
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ShopService(
    private val giftBizApi: GiftBizApi,
    private val productRepo: ProductRepo
) {
    suspend fun getProduct(
        page: Int = 1, pageSize: Int = 20,
        showActiveOnly: Boolean = false,
        query: String? = null
    ): Pair<List<Product>, Long> {
        val pageable = Pageable.ofSize(pageSize).withPage(page - 1)
        if (showActiveOnly) {
            if (query != null) {
                return Pair(productRepo.findByNameContainingAndIsActive(query, true, pageable).map { it.toProduct() }
                    .toList(), 20)
            }
            return Pair(
                productRepo.findByIsActive(true, pageable).map { it.toProduct() }.toList(),
                productRepo.countByIsActive(true)
            )
        }

        if (query != null) {
            return Pair(productRepo.findByNameContaining(query, pageable).map { it.toProduct() }.toList(), 20)
        }
        return Pair(productRepo.findAllBy(pageable).map { it.toProduct() }.toList(), productRepo.count())
    }


    suspend fun importProducts(page: Int = 1, pageSize: Int = 20) {
        var pageNum = page
        while (true) {
            val res = giftBizApi.getGoodsList(pageNum, pageSize)
            val convertedRes = res.result?.goodsList?.map { it.toProduct() } ?: emptyList()
            val ids = productRepo.findAllById(convertedRes.map { it.code }).toList().map { it.id }
            val payload = convertedRes.map { ProductModel.fromProduct(it) }
            payload.forEach {
                it.isNewRecord = !ids.contains(it.code)
            }
            productRepo.saveAll(payload).toList()
            val total = res.result?.listNum ?: 0
            if (total < pageSize * pageNum) {
                break
            }
            pageNum++
        }
    }
}