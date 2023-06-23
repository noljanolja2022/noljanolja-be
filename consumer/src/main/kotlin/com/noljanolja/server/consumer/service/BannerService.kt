package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toBanner
import org.springframework.stereotype.Component

@Component
class BannerService(
    private val coreApi: CoreApi,
) {
    suspend fun getBanners(
        page: Int,
        pageSize: Int,
    ) = coreApi.getBanners(
        page = page,
        pageSize = pageSize,
    ).let { (coreBanners, pagination) ->
        Pair(
            coreBanners.map { it.toBanner() },
            pagination
        )
    }
}