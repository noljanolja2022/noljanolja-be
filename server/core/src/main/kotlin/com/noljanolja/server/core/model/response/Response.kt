package com.noljanolja.server.core.model.response

import com.noljanolja.server.common.rest.Paging
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.model.AuthUser
import com.noljanolja.server.core.model.CoreAnnouncement
import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.TokenData
import kotlinx.serialization.Serializable

@Serializable
data class GetAnnouncementsResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: List<CoreAnnouncement>,
    override val paging: Paging?,
) : Response<List<CoreAnnouncement>>()

@Serializable
data class GetUserResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: CoreUser,
) : Response<CoreUser>()

@Serializable
data class GetUsersResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: List<CoreUser>,
    override val paging: Paging,
) : Response<List<CoreUser>>()

@Serializable
data class GetAuthUserResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: AuthUser,
) : Response<AuthUser>()

@Serializable
data class GetTokenDataResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: TokenData,
) : Response<TokenData>()