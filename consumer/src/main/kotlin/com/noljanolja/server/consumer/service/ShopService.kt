package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.Product
import org.springframework.stereotype.Component

@Component
class ShopService(
    private val coreApi: CoreApi,
) {
    suspend fun getProducts(page: Int = 1, pageSize: Int = 20, query: String? = null): Response<List<Product>> {
        return coreApi.getProducts(page, pageSize, query)
    }
}