package com.noljanolja.server.consumer.model

import com.noljanolja.server.core.model.AuthUser
import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.Gender
import kotlinx.serialization.Serializable

@Serializable
internal data class UserInfo(
    val id: String,
    val name: String,
    val avatar: String,
    val pushToken: String,
    val phone: String,
    val email: String,
    val isEmailVerified: Boolean,
    val gender: Gender,
    val preferences: Preference
) {
    @Serializable
    data class Preference(
        val pushNotiEnabled: Boolean,
        val collectAndUsePersonalInfo: Boolean,
    )
}

internal fun CoreUser.toUserInfo(additionalInfo: AuthUser) = UserInfo(
    id = firebaseUserId,
    name = name,
    avatar = avatar,
    pushToken = pushToken,
    phone = additionalInfo.phone,
    email = additionalInfo.email,
    gender = gender,
    preferences = UserInfo.Preference(
        pushNotiEnabled = preferences.pushNotiEnabled,
        collectAndUsePersonalInfo = preferences.collectAndUsePersonalInfo
    ),
    isEmailVerified = additionalInfo.isEmailVerified
)