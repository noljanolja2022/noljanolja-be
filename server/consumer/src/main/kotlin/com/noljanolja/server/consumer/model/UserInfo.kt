package com.noljanolja.server.consumer.model

import com.noljanolja.server.common.model.AuthUser
import com.noljanolja.server.common.model.CoreUser
import kotlinx.serialization.Serializable

@Serializable
internal data class UserInfo(
    val id: String,
    val name: String,
    val profileImage: String,
    val pushToken: String,
    val pushNotiEnabled: Boolean,
    val phone: String,
    val email: String,
    val isEmailVerified: Boolean,
)

internal fun CoreUser.toUserInfo(additionalInfo: AuthUser) = UserInfo(
    id = firebaseUserId,
    name = name,
    profileImage = profileImage,
    pushToken = pushToken,
    pushNotiEnabled = pushNotiEnabled,
    phone = additionalInfo.phone,
    email = additionalInfo.email,
    isEmailVerified = additionalInfo.isEmailVerified,
)