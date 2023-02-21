package com.noljanolja.server.common.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TokenData(
    val userId: String,
    val roles: List<AuthUser.CustomClaim.Role>,
) {
    @Transient
    var bearerToken: String = ""
}