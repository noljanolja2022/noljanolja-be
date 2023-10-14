package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.auth.AuthApi
import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.adapter.core.CoreUser
import com.noljanolja.server.admin.adapter.core.toAdminUser
import com.noljanolja.server.admin.model.CoreUserUpdateReq
import com.noljanolja.server.admin.model.CreateUserRequest
import com.noljanolja.server.admin.model.User
import com.noljanolja.server.admin.model.UserActivationRequest
import com.noljanolja.server.common.rest.Response
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun createAdminUser(
        token: String,
        createUserReq: CreateUserRequest
    ): User {
        return authApi.createUser(token, createUserReq).toUser()
    }

    suspend fun createUser(
        token: String,
        createUserReq: CreateUserRequest
    ): User? {
        val createdUser = authApi.createUser(token, createUserReq)
        val coreUser = coreApi.createUser(
            CoreUser(
                id = createdUser.id,
                email = createdUser.email,
            )
        )
        return coreUser.toAdminUser()
    }

    suspend fun getUsers(page: Int, pageSize: Int, query: String?): Response<List<CoreUser>> {
        var cleanedQuery = query?.trim()
        if (query != null && query == "") {
            cleanedQuery = null
        }
        val res = coreApi.getUsers(page, pageSize, cleanedQuery)
        return res
    }

    suspend fun updateUser(userId: String, req: UserActivationRequest): User {
        return coreApi.updateUser(userId, CoreUserUpdateReq(
            isActive = req.isActive
        )).toAdminUser()
    }

    suspend fun deleteUser(userId: String) {
        coreApi.deleteUser(userId)
    }
}