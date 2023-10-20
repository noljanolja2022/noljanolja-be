package com.noljanolja.server.core.repo.media

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("video_generated_comments")
data class VideoGeneratedComment(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("content")
    val content: String,

    @Column("video_id")
    val videoId: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)