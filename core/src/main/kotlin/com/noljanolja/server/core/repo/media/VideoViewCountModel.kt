package com.noljanolja.server.core.repo.media

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("video_view_counts")
data class VideoViewCountModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("video_id")
    val videoId: String = "",

    @Column("view_count")
    var viewCount: Long = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: LocalDate = LocalDate.now(),
)