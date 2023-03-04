package com.noljanolja.server.consumer.model

import com.noljanolja.server.core.model.Gender
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val gender: Gender? = null,
    val dob: Instant? = null,
    val preference: UserInfo.Preference? = null
)

