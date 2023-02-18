package com.noljanolja.server.auth.service

import com.google.firebase.auth.FirebaseAuth
import com.noljanolja.server.common.model.AuthUser as UserModel
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
    ): UserModel {
        return firebaseAuth.getUser(uid).let { userRecord ->
            UserModel(
                id = userRecord.uid,
                name = userRecord.displayName.orEmpty(),
                profileImage = userRecord.photoUrl.orEmpty(),
                roles = listOf(
                    try {
                        UserModel.CustomClaim.Role.valueOf(
                            userRecord.customClaims[CUSTOM_CLAIM_KEY_ROLE].toString().uppercase()
                        )
                    } catch (e: Exception) {
                        UserModel.CustomClaim.Role.CONSUMER
                    }
                )
            )
        }
    }
}