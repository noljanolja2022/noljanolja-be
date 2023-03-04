package com.noljanolja.server.consumer.service

import com.noljanolja.server.common.model.TokenData
import com.noljanolja.server.common.model.UpdateFirebaseUserRequest
import com.noljanolja.server.consumer.adapter.auth.AuthApi
import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.UpdateUserRequest
import com.noljanolja.server.consumer.model.User
import com.noljanolja.server.consumer.model.request.UpsertUserRequest
import com.noljanolja.server.consumer.rest.CoreServiceError
import org.springframework.stereotype.Component

@Component
class UserService(
    private val authApi: AuthApi,
    private val coreApi: CoreApi,
) {
    suspend fun getMyInfo(tokenData: TokenData): User {
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
                )
            ).data
        }
    }

    suspend fun updateUser(tokenData: TokenData, updateUser: UpdateUserRequest) : User {
        var currentFBUser = authApi.getUserInfo(tokenData.bearerToken).data
        if (updateUser.name != null || updateUser.email != null) {
            currentFBUser = authApi.updateUserInfo(tokenData.bearerToken, UpdateFirebaseUserRequest(
                name = updateUser.name ?: currentFBUser.name,
                email = updateUser.email ?: currentFBUser.email
            )).data
        }
        val res = coreApi.upsertUser(
            UpsertUserRequest(
                firebaseUserId = tokenData.userId,
                name = updateUser.name ?: currentFBUser.name,
                email = updateUser.email ?: currentFBUser.email,
                gender = updateUser.gender,
                dob = updateUser.dob,
                isEmailVerified = currentFBUser.isEmailVerified
            ) .apply {
                if (updateUser.preferences != null)
                preferences = User.Preference(
                    pushNotiEnabled = updateUser.preferences.pushNotiEnabled,
                    collectAndUsePersonalInfo = updateUser.preferences.collectAndUsePersonalInfo,
                )
            }
        )
        return res.data
    }
}