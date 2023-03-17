package com.noljanolja.server.core.repo.user

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDevicesRepo : CoroutineCrudRepository<UserDeviceModel, Long> {
    fun findAllByUserId(userId: String): Flow<UserDeviceModel>
    suspend fun findByUserIdAndDeviceType(userId: String, deviceType: String): UserDeviceModel?
    suspend fun deleteByUserId(userId: String)
}
