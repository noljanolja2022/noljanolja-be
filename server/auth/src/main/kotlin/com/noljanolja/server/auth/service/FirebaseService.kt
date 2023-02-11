package com.noljanolja.server.auth.service

import com.google.firebase.auth.FirebaseAuth
import com.noljanolja.server.auth.model.User
import org.springframework.stereotype.Component

@Component
class FirebaseService(
    private val firebaseAuth: FirebaseAuth,
) {
    companion object {
        const val CUSTOM_CLAIM_KEY_ROLE = "role"
    }

    suspend fun getUserInfo(
        uid: String,
    ): User {
        return firebaseAuth.getUser(uid).let { userRecord ->
            User(
                uid = userRecord.uid,
                phone = userRecord.phoneNumber.orEmpty(),
                email = userRecord.email.orEmpty(),
                isEmailVerified = userRecord.isEmailVerified,
                name = userRecord.displayName.orEmpty(),
                photoUrl = userRecord.photoUrl.orEmpty(),
                customClaims = userRecord.customClaims.let {
                    User.CustomClaim(
                        role = try {
                            User.CustomClaim.Role.valueOf(it[CUSTOM_CLAIM_KEY_ROLE].toString())
                        } catch (e: Exception) {
                            User.CustomClaim.Role.CONSUMER
                        }
                    )
                }
            )
        }
    }
}