package com.noljanolja.server.admin.service

import com.noljanolja.server.core.model.TokenData
import com.noljanolja.server.core.model.request.UpsertUserRequest
import com.noljanolja.server.admin.adapter.auth.AuthApi
import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.UserInfo
import com.noljanolja.server.admin.model.toUserInfo
import com.noljanolja.server.admin.rest.CoreServiceError
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    internal suspend fun getMyInfo(tokenData: TokenData): UserInfo {
        return try {
            coreApi.getUserInfo(tokenData.userId).data
        } catch (e: CoreServiceError.UserNotFound) {
            val firebaseUser = authApi.getUserInfo(tokenData.bearerToken).data
            coreApi.upsertUser(
                UpsertUserRequest(
                    firebaseUserId = tokenData.userId,
                    name = firebaseUser.name,
                    profileImage = firebaseUser.profileImage,
                )
            ).data
        }.toUserInfo()
    }
}