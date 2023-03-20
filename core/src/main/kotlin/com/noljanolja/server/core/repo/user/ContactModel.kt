package com.noljanolja.server.core.repo.user

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("contacts")
data class ContactModel(
    @Id
    @Column("id")
    val id: Long,

    @Column("country_code")
    val countryCode: String?,

    @Column("phone_number")
    val phoneNumber: String?,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)
