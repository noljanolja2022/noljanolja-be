package com.noljanolja.server.consumer.model.request

import com.noljanolja.server.consumer.model.User
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UpsertUserRequest (
    val firebaseUserId: String,
    val name: String? = null,
    val gender: User.Gender? = null,
    val email: String? = null,
    val dob: Instant? = null,
    val avatar: String? = null,
    val phone: String? = null,
    var preferences: User.Preference? = null,
    val profileImage: String? = null,
    val pushToken: String? = null,
    val isEmailVerified: Boolean? = null
)