package com.noljanolja.server.core.model.request

import com.noljanolja.server.common.validator.StringValidator
import com.noljanolja.server.common.validator.Validator
import com.noljanolja.server.common.validator.validate
import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.Gender
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UpsertUserRequest (
    val firebaseUserId: String,
    val name: String? = null,
    val gender: Gender? = null,
    val email: String? = null,
    val dob: Instant? = null,
    val avatar: String? = null,
    val phone: String? = null,
    var preferences: CoreUser.Preference? = null,
    val profileImage: String? = null,
    val pushToken: String = "",
    val isEmailVerified: Boolean? = null
) {
    init {
        mutableListOf<Validator>().apply {
            add(StringValidator(
                obj = firebaseUserId,
                fieldName = "firebaseUseId",
                required = true,
            ))
            add(StringValidator(
                obj = name,
                fieldName = "name",
                required = false,
            ))
            add(StringValidator(
                obj = profileImage,
                fieldName = "profileImage",
                required = false,
            ))
            add(StringValidator(
                obj = pushToken,
                fieldName = "pushToken",
                required = false,
            ))
        }.validate()
    }
}