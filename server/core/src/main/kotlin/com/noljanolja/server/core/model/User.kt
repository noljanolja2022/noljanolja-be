package com.noljanolja.server.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    val id: String,
    val firebaseUserId: String,
    var name: String,
    var profileImage: String,
    var pushToken: String,
    var pushNotiEnabled: Boolean,
)