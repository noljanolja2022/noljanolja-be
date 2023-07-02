package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.Banner
import com.noljanolja.server.admin.model.UpsertBannerRequest
import com.noljanolja.server.admin.model.toBanner
import com.noljanolja.server.common.model.Pagination
import org.springframework.stereotype.Component

@Component
class BannerService(
    private val coreApi: CoreApi
) {
    suspend fun getBanners(
        page: Int,
        pageSize: Int,
        isActive: Boolean? = null,
        name: String? = null,
    ) : Pair<List<Banner>, Pagination?> {
        val res = coreApi.getBanners(page, pageSize, isActive)
        val data = res.data.orEmpty().map { it.toBanner() }
        return Pair(data, res.pagination)
    }

    suspend fun getBannerDetail(
        bannerId: Long,
    ): Banner? {
        return null
    }

    suspend fun upsertBanner(payload: UpsertBannerRequest): Banner {
        return coreApi.updateBanner(payload).data!!.toBanner()
    }

    suspend fun deleteBanner(
        bannerId: Long,
    ) {
        coreApi.deleteBanner(bannerId)
    }
}