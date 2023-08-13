package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.config.language.Translator
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.model.User
import com.noljanolja.server.consumer.rest.request.*
import com.noljanolja.server.consumer.rest.response.AssignReferralResponse
import com.noljanolja.server.consumer.service.GoogleStorageService
import com.noljanolja.server.consumer.service.UserService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.nio.ByteBuffer
import java.time.LocalDate

@Component
class UserHandler(
    private val googleStorageService: GoogleStorageService,
    private val userService: UserService,
    private val translator: Translator,
) {
    companion object {
        const val PART_NAME_FIELD = "field"
        const val PART_FILES_FIELD = "files"
    }

    suspend fun getCurrentUser(request: ServerRequest): ServerResponse {
        val user = userService.getCurrentUser(AuthUserHolder.awaitUser())
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun updateCurrentUser(request: ServerRequest): ServerResponse {
        val req = request.awaitBodyOrNull<UpdateCurrentUserRequest>() ?: throw RequestBodyRequired
        val currentUserId = AuthUserHolder.awaitUser().id
        val user = userService.updateCurrentUser(currentUserId, req)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun deleteCurrentUser(request: ServerRequest): ServerResponse {
        val currentUser = AuthUserHolder.awaitUser()
        userService.deleteFirebaseUser(currentUser.bearerToken)
        userService.deleteCurrentUser(currentUser.id)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun uploadCurrentUserData(request: ServerRequest): ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val currentUserId = AuthUserHolder.awaitUser().id
        val fieldType = try {
            UploadType.valueOf((reqData[PART_NAME_FIELD]?.firstOrNull() as? FormFieldPart)?.value() ?: "")
        } catch (e: Exception) {
            throw DefaultBadRequestException(null)
        }
        return when (fieldType) {
            UploadType.AVATAR -> {
                val avatar =
                    (reqData[PART_FILES_FIELD]?.firstOrNull() as? FilePart) ?: throw DefaultBadRequestException(
                        Exception("")
                    )
                val fileExtension = avatar.filename().split(".").last()
                val res = googleStorageService.uploadFile(
                    path = "users/${currentUserId}/avatar.$fileExtension",
                    contentType = "image/$fileExtension",
                    content = avatar.content().asFlow().map {
                        val buffer = ByteBuffer.allocate(it.capacity())
                        it.toByteBuffer(buffer)
                        buffer
                    },
                    isPublicAccessible = true
                )
                userService.updateCurrentUser(
                    currentUserId, UpdateCurrentUserRequest(
                        avatar = res.path
                    )
                )
                ServerResponse
                    .ok()
                    .bodyValueAndAwait(
                        Response(data = res)
                    )
            }
        }
    }

    suspend fun getCurrentUserContacts(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: 100
        val contacts = userService.getUserContacts(AuthUserHolder.awaitUser().id, page, pageSize)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = contacts)
            )
    }

    suspend fun syncCurrentUserContact(request: ServerRequest): ServerResponse {
        val currentUserId = AuthUserHolder.awaitUser().id
        val syncCurrentUserContactRequest = request.awaitBodyOrNull<SyncUserContactsRequest>()
            ?: throw RequestBodyRequired
        val friends = userService.syncUserContacts(currentUserId, syncCurrentUserContactRequest.contacts)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = friends)
            )
    }

    suspend fun findUserByPhone(request: ServerRequest): ServerResponse {
        val phoneNumber = request.queryParamOrNull("phoneNumber")
        val friendId = request.queryParamOrNull("friendId")
        if (phoneNumber.isNullOrBlank() && friendId.isNullOrBlank()) {
            return ServerResponse
                .ok()
                .bodyValueAndAwait(
                    Response(data = emptyList<User>())
                )
        }
        val res = if (!friendId.isNullOrBlank())
            listOf(userService.findUserById(friendId))
        else userService.findUsers(phoneNumber)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = res)
            )
    }

    suspend fun sendFriendRequest(request: ServerRequest): ServerResponse {
        val reqBody = request.awaitBodyOrNull<AddFriendRequest>()
            ?: throw RequestBodyRequired
        userService.sendFriendRequest(AuthUserHolder.awaitUser().id, reqBody)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun blockUser(request: ServerRequest): ServerResponse {
        userService.blockUser(
            userId = AuthUserHolder.awaitUser().id,
            request = request.awaitBodyOrNull() ?: throw RequestBodyRequired,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun getBlackList(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: 1
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: 100
        val (users, pagination) = userService.getBlackList(
            userId = AuthUserHolder.awaitUser().id,
            page = page,
            pageSize = pageSize,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = users,
                    pagination = pagination,
                )
            )
    }

    suspend fun checkin(request: ServerRequest): ServerResponse {
        val nextRewardConfig = userService.checkin(
            AuthUserHolder.awaitUser().id,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    message = translator.localize("NEXT_CHECKIN_REWARD_POINTS", arrayOf(nextRewardConfig.rewardPoints)),
                    data = nextRewardConfig,
                )
            )
    }

    suspend fun getMyCheckinProgresses(request: ServerRequest): ServerResponse {
        val localDate = try {
            LocalDate.parse(request.queryParamOrNull("date").orEmpty())
        } catch (err: Throwable) {
            null
        }
        val progresses = userService.getMyCheckinProgresses(
            userId = AuthUserHolder.awaitUser().id,
            localDate = localDate,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = progresses,
                )
            )
    }

    suspend fun assignReferral(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<AssignReferralRequest>() ?: throw RequestBodyRequired
        val rewardPoints = userService.assignReferral(
            userId = AuthUserHolder.awaitUser().id,
            referredByCode = payload.referredByCode,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = AssignReferralResponse(rewardPoints)
                )
            )
    }
}