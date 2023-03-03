package com.noljanolja.server.core.repo.user

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import com.noljanolja.server.core.model.CoreUser
import java.util.*

@Table("users")
data class UserModel(
    @Id
    @Column("id")
    val uid: UUID = UUID.randomUUID(),

    @Column("firebase_user_id")
    var firebaseUserId: String = "",

    @Column("name")
    var name: String = "",

    @Column("profile_image")
    var profileImage: String = "",

    @Column("push_token")
    var pushToken: String = "",

    @Column("push_noti_enabled")
    var pushNotiEnabled: Boolean = false,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Clock.System.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Clock.System.now(),
) : Persistable<UUID> {
    @Transient
    var isNewRecord: Boolean = false
    override fun getId(): UUID = uid

    override fun isNew(): Boolean = isNewRecord
}

fun UserModel.toUser() = CoreUser(
    id = uid.toString(),
    firebaseUserId = firebaseUserId,
    name = name,
    profileImage = profileImage,
    pushToken = pushToken,
    pushNotiEnabled = pushNotiEnabled,
)