package com.noljanolja.server.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    val email: String,
    val isEmailVerified: Boolean,
    val phone: String,
    val name: String,
    val photoUrl: String,
    val customClaims: CustomClaim? = null,
) {
    @Serializable
    data class CustomClaim(
        val role: Role? = null,
    ) {
        enum class Role {
            ADMIN,
            CONSUMER,
        }
    }
}