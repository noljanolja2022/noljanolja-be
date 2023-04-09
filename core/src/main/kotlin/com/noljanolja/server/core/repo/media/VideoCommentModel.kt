package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.VideoComment
import com.noljanolja.server.core.repo.user.UserModel
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("video_comments")
data class VideoCommentModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("comment")
    val comment: String,

    @Column("video_id")
    val videoId: String,

    @Column("commenter_id")
    val commenterId: String,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    @Transient
    var commenter = UserModel()
}

fun VideoCommentModel.toVideoComment() = VideoComment(
    id = id,
    comment = comment,
    commenter = VideoComment.VideoCommenter(
        name = commenter.name,
        avatar = commenter.avatar.orEmpty(),
    )
)