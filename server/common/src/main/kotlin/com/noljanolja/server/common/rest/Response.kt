package com.noljanolja.server.common.rest

import com.noljanolja.server.common.model.AuthUser
import com.noljanolja.server.common.model.CoreAnnouncement
import com.noljanolja.server.common.model.CoreUser
import com.noljanolja.server.common.model.TokenData
import kotlinx.serialization.Serializable

abstract class Response<T> {
    abstract val code: Int
    abstract val message: String
    abstract val data: T
    open val paging: Paging? = null
}

@Serializable
data class EmptyResponse(
    val code: Int = 0,
    val message: String = "Success",
)

@Serializable
data class Paging(
    val totalRecords: Long,
    val page: Long,
    val pageSize: Long,
)

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