package com.noljanolja.server.common.rest

import com.noljanolja.server.common.model.FirebaseUser
import com.noljanolja.server.common.model.TokenData
import kotlinx.serialization.Serializable

abstract class Response<T> {
    open val code: Int = 0
    open val message: String = "Success"
    abstract val data: T
    open val paging: Paging? = null
}

@Serializable
data class Paging(
    val totalRecords: Long,
    val page: Long,
    val pageSize: Long,
)

@Serializable
data class EmptyResponse(
    val code: Int = 0,
    val message: String = "Success",
)

@Serializable
data class GetTokenDataResponse(
    override val data: TokenData,
) : Response<TokenData>()

@Serializable
data class GetAuthUserResponse(
    override val data: FirebaseUser,
) : Response<FirebaseUser>()