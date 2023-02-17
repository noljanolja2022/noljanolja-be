package com.noljanolja.server.core.service

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import com.noljanolja.server.core.model.User as UserModel
import java.util.*

@Table("users")
data class User(
    @Id
    @Column("id")
    var uid: UUID = UUID.randomUUID(),

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
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) : Persistable<UUID> {
    @Transient
    var isNewRecord: Boolean = false
    override fun getId(): UUID = uid

    override fun isNew(): Boolean = isNewRecord
}

fun User.toUserModel() = UserModel(
    id = uid.toString(),
    firebaseUserId = firebaseUserId,
    name = name,
    profileImage = profileImage,
    pushToken = pushToken,
    pushNotiEnabled = pushNotiEnabled,
)