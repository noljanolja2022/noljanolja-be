package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.auth.AuthApi
import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.adapter.core.CoreUser
import com.noljanolja.server.admin.adapter.core.toAdminUser
import com.noljanolja.server.admin.model.CreateUserRequest
import com.noljanolja.server.admin.model.User
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun getCurrentUser(userId: String): User? {
        val coreUser = coreApi.getUser(userId)
        return coreUser?.toAdminUser()
    }

    suspend fun createUser(
        token: String,
        createUserReq: CreateUserRequest) : User?  {
        val createdUser = authApi.createUser(token, createUserReq)
        val coreUser = coreApi.upsertUser(CoreUser(
            id = createdUser.id,
            email = createdUser.email,
        ))
        return coreUser.toAdminUser()
    }
}