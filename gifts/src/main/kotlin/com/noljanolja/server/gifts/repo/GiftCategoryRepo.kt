package com.noljanolja.server.gifts.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface GiftCategoryRepo : CoroutineCrudRepository<GiftCategoryModel, Long> {
}