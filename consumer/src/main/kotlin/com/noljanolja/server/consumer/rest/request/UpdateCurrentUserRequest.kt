package com.noljanolja.server.consumer.rest.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.noljanolja.server.consumer.adapter.core.CoreUserPreferences
import com.noljanolja.server.consumer.model.Gender
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateCurrentUserRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatar: String? = null,
    val dob: LocalDate? = null,
    val gender: Gender? = null,
    val preferences: CoreUserPreferences? = null
)

enum class UploadType {
    AVATAR
}