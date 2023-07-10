package com.noljanolja.server.core.rest.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.noljanolja.server.core.model.Gender
import com.noljanolja.server.core.model.UserPreferences
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpsertUserRequest(
    val id: String,
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: UserPreferences? = null,
)

data class UpdateUserRequest(
    val name: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val isActive: Boolean? = null
)