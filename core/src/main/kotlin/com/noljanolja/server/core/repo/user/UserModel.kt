package com.noljanolja.server.core.repo.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.noljanolja.server.core.model.Gender
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserPreferences
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate

@Table("users")
data class UserModel(
    @Id
    @Column("id")
    val _id: String = "",

    @Column("name")
    val name: String = "",

    @Column("avatar")
    val avatar: String? = null,

    @Column("country_code")
    val countryCode: String = "",

    @Column("phone_number")
    val phoneNumber: String = "",

    @Column("email")
    val email: String? = null,

    @Column("dob")
    val dob: LocalDate? = null,

    @Column("gender")
    val gender: Gender? = null,

    @Column("is_active")
    val isActive: Boolean = true,

    @Column("is_reported")
    val isReported: Boolean = false,

    @Column("is_blocked")
    val isBlocked: Boolean = false,

    @Column("preferences")
    val preferences: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) : Persistable<String> {
    @Transient
    var isNewRecord = false
    override fun getId() = _id

    override fun isNew() = isNewRecord

    fun getPhone() = if (countryCode.isNotEmpty() && phoneNumber.isNotEmpty()) "+$countryCode$phoneNumber" else ""

    companion object {
        fun User.toUserModel(objectMapper: ObjectMapper, isNewUser: Boolean): UserModel {
            val phoneNumber = PhoneNumberUtil.getInstance().parse(phone, null)
            return UserModel(
                _id = id,
                name = name,
                avatar = avatar,
                countryCode = phoneNumber.countryCode.toString(),
                phoneNumber = phoneNumber.nationalNumber.toString(),
                email = email,
                dob = dob,
                gender = gender,
                isActive = isActive,
                isReported = isReported,
                isBlocked = isBlocked,
                preferences = objectMapper.writeValueAsString(preferences),
                createdAt = createdAt,
                updatedAt = updatedAt,
            ).apply {
                isNewRecord = isNewUser
            }
        }
    }
}

fun UserModel.toUser(objectMapper: ObjectMapper) = User(
    id = id,
    name = name,
    avatar = avatar,
    phone = getPhone(),
    email = email,
    dob = dob,
    gender = gender,
    isActive = isActive,
    isReported = isReported,
    isBlocked = isBlocked,
    preferences = preferences.takeIf { it.isNotBlank() }?.let {
        objectMapper.readValue(it, UserPreferences::class.java)
    } ?: UserPreferences(),
    createdAt = createdAt,
    updatedAt = updatedAt,
)