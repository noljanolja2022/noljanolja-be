package com.noljanolja.server.consumer.model.response

import com.noljanolja.server.common.rest.Paging
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.model.Announcement
import com.noljanolja.server.consumer.model.User
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponse(
    override val data: User,
) : Response<User>()

@Serializable
data class GetAnnouncementsResponse(
    override val data: List<Announcement>,
    override val paging: Paging,
) : Response<List<Announcement>>()