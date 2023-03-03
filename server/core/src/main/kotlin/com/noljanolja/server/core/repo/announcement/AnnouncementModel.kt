package com.noljanolja.server.core.repo.announcement

import com.noljanolja.server.core.model.CoreAnnouncement
import kotlinx.datetime.Clock
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import kotlinx.datetime.Instant
import java.util.UUID

@Table("announcements")
data class AnnouncementModel(
    @Id
    @Column("id")
    val uid: UUID = UUID.randomUUID(),

    @Column("title")
    val title: String,

    @Column("content")
    val content: String,

    @Column("priority")
    val priority: CoreAnnouncement.Priority,

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

fun AnnouncementModel.toAnnouncementModel() = CoreAnnouncement(
    id = uid.toString(),
    title = title,
    content = content,
    priority = priority,
    date = createdAt,
)