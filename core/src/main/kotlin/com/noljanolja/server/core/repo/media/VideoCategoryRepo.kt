package com.noljanolja.server.core.repo.media

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoCategoryRepo : CoroutineCrudRepository<VideoCategoryModel, String> {

}