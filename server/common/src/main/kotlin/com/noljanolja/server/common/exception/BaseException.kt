package com.noljanolja.server.common.exception

open class BaseException(
    open val code: Int,
    override val message: String,
    override val cause: Throwable?,
) : Exception(message, cause)

data class DefaultBadRequestException(
    override val cause: Throwable?,
) : BaseException(
    code = 400_000,
    message = "Bad Request",
    cause = cause,
)

object UserNotFound : BaseException(
    code = 404_001,
    message = "User not found",
    cause = null
)

data class InvalidParamsException(
    val paramName: String
) : BaseException(
    code = 400_001,
    message = "Invalid $paramName",
    cause = IllegalArgumentException("Invalid $paramName")
)

object RequestBodyRequired : BaseException(
    code = 400_002,
    message = "Request body required",
    cause = null,
)

data class DefaultNotFoundException(
    override val cause: Throwable?,
) : BaseException(
    code = 404_000,
    message = "Not Found",
    cause = cause,
)

data class DefaultInternalErrorException(
    override val cause: Throwable?,
) : BaseException(
    code = 500_000,
    message = "Internal Server Error",
    cause = cause,
)

data class ExternalServiceException(
    override val cause: Throwable?,
) : BaseException(
    code = 500_001,
    message = "Internal Server Error",
    cause = cause,
)

sealed class FirebaseException(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {
    companion object {
        const val FAILED_TO_VERIFY_TOKEN = 401_001
    }

    class FailedToVerifyToken(
        cause: Throwable? = null,
    ) : FirebaseException(
        FAILED_TO_VERIFY_TOKEN,
        "[Firebase] Failed to verify id token",
        cause,
    )
}

open class ValidationDataError(
    code: Int,
    message: String,
    cause: Throwable?,
) : BaseException(code, message, cause) {

    companion object {
        const val FIELD_EXCEED_MAX_LENGTH_STATUS_CODE = 400_100
        const val FIELD_EXCEED_MAX_VALUE_STATUS_CODE = 400_101
        const val FIELD_NUMBER_OUT_OF_RANGE = 400_102
        const val FIELD_IS_INVALID = 400_103
        const val FIELD_LENGTH_ERROR_STATUS_CODE = 400_104
        const val FIELD_IS_REQUIRED = 400_105
    }

    /** In case field is exceeding of max length */
    class FieldExceedingMaxLength(
        val fieldName: String,
    ) : ValidationDataError(
        FIELD_EXCEED_MAX_LENGTH_STATUS_CODE,
        "Field $fieldName is exceeding the max length",
        null,
    )

    /** In case field is exceeding of max value */
    class FieldExceedingMaxValue(
        val fieldName: String,
    ) : ValidationDataError(
        FIELD_EXCEED_MAX_VALUE_STATUS_CODE,
        "Field $fieldName is exceeding the max value",
        null,
    )

    /** In case field is not valid */
    class FieldNumberOutOfRange(
        val fieldName: String,
        val minimum: String,
        val maximum: String,
    ) : ValidationDataError(
        FIELD_NUMBER_OUT_OF_RANGE,
        "Field $fieldName is out of range [$minimum, $maximum]",
        null,
    )

    /** In case field is null or empty while it should not */
    class FieldIsInvalid(
        val fieldName: String,
    ) : ValidationDataError(
        FIELD_IS_INVALID,
        "Field $fieldName is invalid (wrong format or null/empty while it should not)",
        null,
    )

    /** In case string field's length is out of Range */
    class FieldLengthOutOfRange(
        val fieldName: String,
        val minLength: String,
        val maxLength: String,
    ) : ValidationDataError(
        FIELD_LENGTH_ERROR_STATUS_CODE,
        "Field ${fieldName}'s length is out of range [$minLength, $maxLength]",
        null,
    )

    class FieldRequired(
        val fieldName: String,
    ) : ValidationDataError(
        FIELD_IS_REQUIRED,
        "Field $fieldName is required",
        null,
    )
}