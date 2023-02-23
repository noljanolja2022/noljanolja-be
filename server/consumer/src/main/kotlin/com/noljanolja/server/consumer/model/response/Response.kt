package com.noljanolja.server.consumer.model.response

import com.noljanolja.server.common.rest.Paging
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.model.Announcement
import com.noljanolja.server.consumer.model.UserInfo
import kotlinx.serialization.Serializable

@Serializable
internal data class GetMyInfoResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: UserInfo,
) : Response<UserInfo>()

@Serializable
internal data class GetAnnouncementsResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: List<Announcement>,
    override val paging: Paging,
) : Response<List<Announcement>>()