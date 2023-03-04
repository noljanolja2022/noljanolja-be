package com.noljanolja.server.core.repo.user

import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.Gender
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("users")
data class UserModel(
    @Id
    @Column("id")
    val uid: Long? = null,

    @Column("firebase_user_id")
    var firebaseUserId: String = "",

    @Column("name")
    var name: String = "",

    @Column("profile_image")
    var avatar: String? = null,

    @Column("email")
    var email: String? = null,

    @Column("phone")
    var phone: String? = null,

    @Column("is_email_verified")
    var isEmailVerified: Boolean = false,

    @Column("dob")
    val dob: Instant? = null,

    @Column("gender")
    var gender: Gender? = null,

    @Column("push_token")
    var pushToken: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Clock.System.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Clock.System.now(),

    // SETTINGS
    @Column("collect_and_user_personal_info")
    var collectAndUsePersonalInfo: Boolean = false,

    @Column("push_noti_enabled")
    var pushNotiEnabled: Boolean = false,
) : Persistable<Long> {
    override fun getId(): Long? = uid

    override fun isNew() = id?.takeIf { it > 0 } == null
}

fun UserModel.toUser() = CoreUser(
    id = uid.toString(),
    firebaseUserId = firebaseUserId,
    name = name,
    email= email,
    avatar = avatar,
    dob = dob,
    gender = gender ?: Gender.Other,
    phone = phone,
    isEmailVerified = isEmailVerified,
    pushToken = pushToken,
    preferences = CoreUser.Preference(
        pushNotiEnabled = pushNotiEnabled,
        collectAndUsePersonalInfo = collectAndUsePersonalInfo
    )
)