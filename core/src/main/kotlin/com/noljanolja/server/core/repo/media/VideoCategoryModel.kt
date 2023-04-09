package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.Video
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("video_categories")
data class VideoCategoryModel(
    @Id
    @Column("id")
    val _id: String = "",

    @Column("title")
    var title: String = "",

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) : Persistable<String> {
    @Transient
    var isNewRecord: Boolean = false

    override fun getId() = _id

    override fun isNew() = isNewRecord
}

fun VideoCategoryModel.toVideoCategory() = Video.Category(
    id = id,
    title = title,
)