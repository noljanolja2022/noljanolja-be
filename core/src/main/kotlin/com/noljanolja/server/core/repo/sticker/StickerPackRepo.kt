package com.noljanolja.server.core.repo.sticker

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface StickerPackRepo : CoroutineCrudRepository<StickerPackModel, Long> {

}