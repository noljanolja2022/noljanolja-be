package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.auth.AuthApi
import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.adapter.core.toAdminUser
import com.noljanolja.server.admin.model.User
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun getCurrentUser(userId: String): User? {
        // TODO set user if not exist
        return coreApi.getUser(userId)?.toAdminUser()
    }
}