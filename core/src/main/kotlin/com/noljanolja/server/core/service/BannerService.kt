package com.noljanolja.server.core.service

import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Banner
import com.noljanolja.server.core.repo.banner.BannerModel
import com.noljanolja.server.core.repo.banner.BannerRepo
import com.noljanolja.server.core.repo.banner.toBanner
import com.noljanolja.server.core.rest.request.UpsertBannerRequest
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class BannerService(
    private val bannerRepo: BannerRepo,
) {
    suspend fun upsertBanner(payload: UpsertBannerRequest): Banner {
        val banner = payload.id?.let { bannerRepo.findById(it) ?: throw Error.BannerNotFound } ?: BannerModel()
        return bannerRepo.save(
            banner.apply {
                title = payload.title
                description = payload.description
                content = payload.content
                image = payload.image
                isActive = payload.isActive
                priority = payload.priority
                action = payload.action
                startTime = payload.startTime
                endTime = payload.endTime
            }
        ).toBanner()
    }

    suspend fun getBanners(
        page: Int,
        pageSize: Int,
        isActive: Boolean? = null,
        name: String? = null,
    ) = Pair(
        bannerRepo.findAllBy(
            title = name,
            isActive = isActive,
            limit = pageSize,
            offset = (page - 1) * pageSize,
        ).toList().map { it.toBanner() },
        bannerRepo.countAllBy(
            isActive = isActive,
        )
    )

    suspend fun getBannerDetail(
        bannerId: Long,
    ): Banner {
        return (bannerRepo.findById(bannerId) ?: throw Error.BannerNotFound).toBanner()
    }

    suspend fun deleteBanner(
        bannerId: Long,
    ) {
        bannerRepo.findById(bannerId) ?: throw Error.BannerNotFound
        bannerRepo.deleteById(bannerId)
    }
}