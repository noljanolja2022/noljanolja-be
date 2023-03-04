package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.UpdateUserRequest
import com.noljanolja.server.consumer.model.UserInfo
import com.noljanolja.server.consumer.model.toUserInfo
import com.noljanolja.server.consumer.rest.CoreServiceError
import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.TokenData
import com.noljanolja.server.core.model.request.UpsertUserRequest
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
                    email = firebaseUser.email,
                    phone = firebaseUser.phone,
                    avatar = firebaseUser.profileImage,
                    profileImage = firebaseUser.profileImage,
                    pushToken = "",
                    pushNotiEnabled = false,
                )
            ).data
        }.toUserInfo(firebaseUser)
    }

    internal suspend fun updateUser(tokenData: TokenData, updateUser: UpdateUserRequest) : UserInfo {
        authApi.getUserInfo(tokenData.bearerToken).data
        val res = coreApi.upsertUser(
            UpsertUserRequest(
                firebaseUserId = tokenData.userId,
                name = updateUser.name,
                email = updateUser.email,
                gender = updateUser.gender,
                dob = updateUser.dob,
            ) .apply {
                if (updateUser.preference != null)
                preferences = CoreUser.Preference(
                    pushNotiEnabled = updateUser.preference.pushNotiEnabled,
                    collectAndUsePersonalInfo = updateUser.preference.collectAndUsePersonalInfo,
                )
            }
        )
        return res.data.toUserInfo()
    }
}