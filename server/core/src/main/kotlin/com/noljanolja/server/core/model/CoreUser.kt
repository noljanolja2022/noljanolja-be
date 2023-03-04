package com.noljanolja.server.core.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CoreUser(
    val id: String,
    val firebaseUserId: String,
    var name: String,
    var phone: String?,
    var email: String?,
    var dob: Instant?,
    var gender: Gender,
    var avatar: String,
    var pushToken : String,
    val preferences: Preference,
    var isEmailVerified: Boolean
) {
    @Serializable
    data class Preference(
        val pushNotiEnabled: Boolean = false,
        val collectAndUsePersonalInfo: Boolean = false,
    )
}

enum class Gender {
    Male, Female, Other
}