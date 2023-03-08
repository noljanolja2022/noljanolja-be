package com.noljanolja.server.consumer.adapter.core.request

import com.noljanolja.server.consumer.model.Gender
import java.time.LocalDate

data class UpdateUserRequest(
    val name: String?,
    val email: String?,
    val gender: Gender?,
    val dob: LocalDate?,

)