package com.noljanolja.server.core.model.response

import com.noljanolja.server.common.rest.Paging
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.model.CoreAnnouncement
import com.noljanolja.server.core.model.CoreUser
import kotlinx.serialization.Serializable

@Serializable
data class GetAnnouncementsResponse(
    override val data: List<CoreAnnouncement>,
    override val paging: Paging?,
) : Response<List<CoreAnnouncement>>()

@Serializable
data class GetUserResponse(
    override val data: CoreUser,
) : Response<CoreUser>()

@Serializable
data class GetUsersResponse(
    override val data: List<CoreUser>,
    override val paging: Paging,
) : Response<List<CoreUser>>()