package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.User
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun getCurrentUser(userId: String): User? {
        // TODO set user if not exist
        return coreApi.getUser(userId)?.toConsumerUser()
    }
}