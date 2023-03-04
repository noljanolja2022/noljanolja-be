package com.noljanolja.server.consumer.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val gender: User.Gender? = null,
    val dob: Instant? = null,
    val preferences: User.Preference? = null
)

