package com.noljanolja.server.common.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val id: String,
    val name: String,
    val profileImage: String,
    val roles: List<CustomClaim.Role>,
) {
    @Serializable
    data class CustomClaim(
        val role: Role,
    ) {
        enum class Role {
            ADMIN,
            STAFF,
            CONSUMER,
        }
    }
}