package com.noljanolja.server.core.repo.message

import com.noljanolja.server.core.model.MessageReactionIcon
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("message_reactions")
data class MessageReactionModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("code")
    val code: String = "",

    @Column("code_inactive")
    val codeInactive: String = "",

    @Column("is_default")
    val isDefault: Boolean = false,

    @Column("description")
    val description: String = "",

    @CreatedDate
    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
)

fun MessageReactionModel.toMessageReactionIcon() = MessageReactionIcon(
    id = id,
    code = code,
    description = description,
    isDefault = isDefault,
    codeInactive = codeInactive,
)