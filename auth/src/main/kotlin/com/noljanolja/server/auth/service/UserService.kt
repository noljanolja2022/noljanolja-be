package com.noljanolja.server.auth.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.noljanolja.server.auth.model.User
import com.noljanolja.server.common.exception.DefaultInternalErrorException
import org.springframework.stereotype.Component

@Component
class UserService(
    private val firebaseAuth: FirebaseAuth,
) {
    companion object {
        const val CUSTOM_CLAIM_KEY_ROLE = "role"
    }

    suspend fun setPermission(userId: String, permission: String = "admin") {
        firebaseAuth.setCustomUserClaims(userId, mapOf(Pair(CUSTOM_CLAIM_KEY_ROLE, permission)))
    }

    suspend fun createUser(userName: String, password: String): User {
        val createReq = UserRecord.CreateRequest().apply {
            setEmail(userName)
            setPassword(password)
        }

        return firebaseAuth.createUser(createReq)?.let { firebaseUser ->
            User.fromFirebaseUser(firebaseUser)
        } ?: throw DefaultInternalErrorException(Error("Unable to create user"))
    }

    suspend fun getUser(
        userId: String,
    ): User {
        return firebaseAuth.getUser(userId).let { firebaseUser ->
            User.fromFirebaseUser(firebaseUser)
        }
    }

    suspend fun deleteUser(
        userId: String,
    ) {
        firebaseAuth.deleteUser(userId)
    }
}