package com.noljanolja.server.auth.model

import com.google.firebase.auth.UserRecord
import com.noljanolja.server.auth.service.UserService

data class User(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val isEmailVerified: Boolean = false,
    val roles: List<Role> = emptyList(),
) {
    enum class Role {
        ADMIN,
        STAFF,
        CONSUMER,
    }

    companion object {
        fun fromFirebaseUser(record: UserRecord): User {
            return User(
                id = record.uid,
                name = record.displayName,
                avatar = record.photoUrl,
                phone = record.phoneNumber,
                email = record.email,
                isEmailVerified = record.isEmailVerified,
                roles = with(record.customClaims[UserService.CUSTOM_CLAIM_KEY_ROLE].toString()) {
                    listOf(
                        Role.values().find { it.name.equals(this, true) }
                            ?: Role.CONSUMER
                    )
                }
            )
        }
    }
}

data class CreateUserRequest(
    val userName: String,
    val password: String,
    val role: String = "admin"
)