package com.noljanolja.server.auth.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.noljanolja.server.common.model.FirebaseUser
import com.noljanolja.server.common.util.enumByNameIgnoreCase
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
    ): FirebaseUser {
        return firebaseAuth.getUser(uid).let { userRecord ->
            FirebaseUser(
                id = userRecord.uid,
                name = userRecord.displayName.orEmpty(),
                profileImage = userRecord.photoUrl.orEmpty(),
                phone = userRecord.phoneNumber.orEmpty(),
                email = userRecord.email.orEmpty(),
                isEmailVerified = userRecord.isEmailVerified,
                roles = listOf(
                    enumByNameIgnoreCase(
                        userRecord.customClaims[CUSTOM_CLAIM_KEY_ROLE].toString(),
                        FirebaseUser.CustomClaim.Role.CONSUMER,
                    )
                )
            )
        }
    }

    suspend fun updateUserInfo(uid: String, name: String?, email: String?) : FirebaseUser  {
        val updateRequest = UserRecord.UpdateRequest(uid).apply {
            if (name != null) setDisplayName(name)
            if (email != null) setEmail(email)
        }
        return firebaseAuth.updateUser(updateRequest).let {
            FirebaseUser(
                id = it.uid,
                name = it.displayName.orEmpty(),
                profileImage = it.photoUrl.orEmpty(),
                phone = it.phoneNumber.orEmpty(),
                email = it.email.orEmpty(),
                isEmailVerified = it.isEmailVerified,
                roles = listOf(
                    enumByNameIgnoreCase(
                        it.customClaims[CUSTOM_CLAIM_KEY_ROLE].toString(),
                        FirebaseUser.CustomClaim.Role.CONSUMER,
                    )
                )
            )
        }
    }
}