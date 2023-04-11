package com.noljanolja.server.core.repo.conversation

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table("conversations_participants")
data class ConversationParticipantModel(
    @Id
    @Column("id")
    var id: Long = 0,

    @Column("participant_id")
    var participantId: String,

    @Column("conversation_id")
    var conversationId: Long
)