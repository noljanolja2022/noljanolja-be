package com.noljanolja.server.core.repo.message

import com.noljanolja.server.core.repo.user.UserModel
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Transient
import java.time.Instant

@Table("messages_participants_reactions")
data class MessageParticipantReactionModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("participant_id")
    val participantId: String,

    @Column("message_id")
    val messageId: Long,

    @Column("reaction_id")
    val reactionId: Long,

    @CreatedDate
    @Column("created_at")
    val createdAt: Instant = Instant.now(),

    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
) {
    @Transient
    var participant: UserModel = UserModel()

    @Transient
    var reaction: MessageReactionModel = MessageReactionModel()
}
