package com.noljanolja.server.admin.model.response

import com.noljanolja.server.admin.model.UserInfo
import com.noljanolja.server.common.rest.Response
import kotlinx.serialization.Serializable

@Serializable
internal data class GetMyInfoResponse(
    override val code: Int = 0,
    override val message: String = "Success",
    override val data: UserInfo,
) : Response<UserInfo>()