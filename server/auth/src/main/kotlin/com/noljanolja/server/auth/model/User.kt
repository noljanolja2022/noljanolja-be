package com.noljanolja.server.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
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