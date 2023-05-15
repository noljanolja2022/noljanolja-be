package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.StickerPack
import com.noljanolja.server.core.repo.sticker.StickerModel
import com.noljanolja.server.core.repo.sticker.StickerPackModel
import com.noljanolja.server.core.repo.sticker.StickerPackRepo
import com.noljanolja.server.core.repo.sticker.StickerRepo
import com.noljanolja.server.core.rest.request.CreateStickerRequest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class StickerService(
    private val stickerPackRepo: StickerPackRepo,
    private val stickerRepo: StickerRepo,
    private val objectMapper: ObjectMapper,
) {
    suspend fun getStickerPack(packId: Long) : StickerPack {
        val stickerPackModel = stickerPackRepo.findById(packId) ?: throw Error.StickerPackNotFound
        val stickers = stickerRepo.findAllByPackId(packId).map { it.toSticker() }.toList()
        return stickerPackModel.toStickerPack(stickers)
    }

    suspend fun getStickerPacks(): List<StickerPack> {
        val stickerPackModel = stickerPackRepo.findAllByIsActive().toList()
        val stickers = stickerRepo.findAll().toList()
        return stickerPackModel.map { pack ->
            pack.toStickerPack(stickers.filter { it.packId == pack.id }.map { it.toSticker() })
        }
    }

    suspend fun createStickerPack(request: CreateStickerRequest): StickerPack {
        val createdPackModel = stickerPackRepo.save(
            StickerPackModel(
                id = 0, name = request.name,
                publisher = request.publisher,
                trayImageFile = request.trayImageFile,
                isAnimated = request.isAnimated
            )
        )
        val stickers = stickerRepo.saveAll(
            request.stickers.map {
                StickerModel.fromSticker(it, createdPackModel.id)
            }
        ).map { it.toSticker() }
        return createdPackModel.toStickerPack(stickers.toList())
    }

    suspend fun disableStickerPack(packId: Long) {
        stickerPackRepo.disableStickerPack(packId)
    }
}