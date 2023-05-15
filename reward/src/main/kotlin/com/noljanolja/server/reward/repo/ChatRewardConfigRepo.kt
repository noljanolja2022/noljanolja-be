package com.noljanolja.server.reward.repo

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRewardConfigRepo : CoroutineCrudRepository<ChatRewardConfigModel, Long> {
    fun findAllByRoomType(
        roomType: RoomType,
    ): Flow<ChatRewardConfigModel>

    suspend fun findByRoomType(
        roomType: RoomType,
    ): ChatRewardConfigModel?
}