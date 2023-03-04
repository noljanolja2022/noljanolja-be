package com.noljanolja.server.common.model

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUser(
    val id: String,
    val name: String?,
    val profileImage: String?,
    val roles: List<CustomClaim.Role>,
    val phone: String?,
    val email: String?,
    val isEmailVerified: Boolean,
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

@Serializable
data class UpdateFirebaseUserRequest(
    val name: String?,
    val email: String?,
)