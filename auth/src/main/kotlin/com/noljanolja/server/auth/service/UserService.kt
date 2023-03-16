package com.noljanolja.server.auth.service

import com.google.firebase.auth.FirebaseAuth
import com.noljanolja.server.auth.model.User
import org.springframework.stereotype.Component

@Component
class UserService(
    private val firebaseAuth: FirebaseAuth,
) {
    companion object {
        const val CUSTOM_CLAIM_KEY_ROLE = "role"
    }

    suspend fun setPermission(userId: String) {
        firebaseAuth.setCustomUserClaims(userId, mapOf(Pair(CUSTOM_CLAIM_KEY_ROLE, "admin")) )
    }

    suspend fun getUser(
        userId: String,
    ): User {
        return firebaseAuth.getUser(userId).let { firebaseUser ->
            User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName,
                avatar = firebaseUser.photoUrl,
                phone = firebaseUser.phoneNumber,
                email = firebaseUser.email,
                isEmailVerified = firebaseUser.isEmailVerified,
                roles = with(firebaseUser.customClaims[CUSTOM_CLAIM_KEY_ROLE].toString()) {
                    listOf(
                        User.Role.values().find { it.name.equals(this, true) } ?: User.Role.CONSUMER
                    )
                }
            )
        }
    }

    suspend fun deleteUser(
        userId: String,
    ) {
        firebaseAuth.deleteUser(userId)
    }
}