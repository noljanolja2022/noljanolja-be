package com.noljanolja.server.common.validator

import com.noljanolja.server.common.exception.BaseException
import com.noljanolja.server.common.exception.ValidationDataError

abstract class Validator {
    open fun validate(): BaseException? {
        return null
    }
}

open class StringValidator(
    private val obj: String?,
    private val fieldName: String,
    private val minLength: Int? = 0,
    private val maxLength: Int? = Int.MAX_VALUE,
    private val required: Boolean = true,
    private val regex: Regex? = null,
) : Validator() {
    override fun validate(): BaseException? = super.validate() ?: when {
        obj.isNullOrBlank() && required ->
            ValidationDataError.FieldIsInvalid(fieldName)

        obj == "null" ->
            ValidationDataError.FieldIsInvalid(fieldName)

        obj != null && minLength != null && obj.length < minLength ->
            ValidationDataError.FieldLengthOutOfRange(fieldName, minLength.toString(), maxLength.toString())

        obj != null && maxLength != null && obj.length > maxLength ->
            ValidationDataError.FieldLengthOutOfRange(fieldName, minLength.toString(), maxLength.toString())

        obj != null && regex != null && !regex.containsMatchIn(obj) ->
            ValidationDataError.FieldIsInvalid(fieldName)

        else -> null
    }
}

fun List<Validator>.validate() {
    forEach { validator ->
        val error = validator.validate()
        if (error != null) throw error
    }
}

const val UUID_REGEXP = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"

fun String.isUUID() = this.matches(UUID_REGEXP.toRegex())