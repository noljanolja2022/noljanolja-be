package com.noljanolja.server.core.repo.user

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_contacts")
data class UserContactModel(
    @Id
    @Column("id")
    val id: Long,

    @Column("user_id")
    val userId: String,

    @Column("contact_id")
    val contactId: Long,

    @Column("contact_name")
    var contactName: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)