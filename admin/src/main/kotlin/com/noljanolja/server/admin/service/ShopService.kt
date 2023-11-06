package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.Product
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component

@Component
class ShopService(
    private val coreApi: CoreApi,
) {
    suspend fun getProducts(page: Int = 1, pageSize: Int = 20, query: String? = null): Response<List<Product>> {
        return coreApi.getProducts(page, pageSize, query)
    }

    suspend fun importProducts() {
        coreApi.importProducts()
    }
}