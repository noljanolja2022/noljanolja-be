package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.model.TokenData
import com.noljanolja.server.common.model.request.UpsertUserRequest
import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.UserInfo
import com.noljanolja.server.consumer.model.toUserInfo
import com.noljanolja.server.consumer.rest.CoreServiceError
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    internal suspend fun getMyInfo(tokenData: TokenData): UserInfo {
        val firebaseUser = authApi.getUserInfo(tokenData.bearerToken).data
        return try {
            coreApi.getUserInfo(tokenData.userId).data
        } catch (e: CoreServiceError.UserNotFound) {
            coreApi.upsertUser(
                UpsertUserRequest(
                    firebaseUserId = tokenData.userId,
                    name = firebaseUser.name,
                    profileImage = firebaseUser.profileImage,
                    pushToken = "",
                    pushNotiEnabled = false,
                )
            ).data
        }.toUserInfo(firebaseUser)
    }
}