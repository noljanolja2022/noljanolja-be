package com.noljanolja.server.consumer.model

import com.noljanolja.server.common.model.CoreUser
import kotlinx.serialization.Serializable

@Serializable
internal data class UserInfo(
    val id: String,
    val name: String,
    val profileImage: String,
    val pushToken: String,
    val pushNotiEnabled: Boolean,
)

internal fun CoreUser.toUserInfo() = UserInfo(
    id = firebaseUserId,
    name = name,
    profileImage = profileImage,
    pushToken = pushToken,
    pushNotiEnabled = pushNotiEnabled,
)