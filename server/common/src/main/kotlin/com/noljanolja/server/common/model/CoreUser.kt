package com.noljanolja.server.common.model

import kotlinx.serialization.Serializable

@Serializable
data class CoreUser(
    val id: String,
    val firebaseUserId: String,
    var name: String,
    var profileImage: String,
    var pushToken: String,
    var pushNotiEnabled: Boolean,
)