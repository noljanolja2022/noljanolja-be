package com.noljanolja.server.admin.model

import com.noljanolja.server.core.model.CoreUser
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    val profileImage: String,
)

fun CoreUser.toUserInfo() = UserInfo(
    id = firebaseUserId,
    name = name,
    profileImage = avatar,
)