package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.rest.request.SyncUserContactsRequest
import com.noljanolja.server.consumer.rest.request.UpdateCurrentUserRequest
import com.noljanolja.server.consumer.rest.request.UploadType
import com.noljanolja.server.consumer.service.GoogleStorageService
import com.noljanolja.server.consumer.service.UserService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.nio.ByteBuffer

@Component
class UserHandler(
    private val googleStorageService: GoogleStorageService,
    private val userService: UserService,
) {
    companion object {
        const val PART_NAME_FIELD = "field"
        const val PART_FILES_FIELD = "files"
    }

    suspend fun getCurrentUser(request: ServerRequest): ServerResponse {
        val user =
            userService.getCurrentUser(AuthUserHolder.awaitUser()) ?: throw DefaultUnauthorizedException(null)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun updateCurrentUser(request: ServerRequest): ServerResponse {
        val reqBody = request.awaitBodyOrNull<UpdateCurrentUserRequest>() ?: throw RequestBodyRequired
        val bearer =
            request.headers().firstHeader(HttpHeaders.AUTHORIZATION) ?: throw DefaultUnauthorizedException(null)
        val currentUser = userService.getFirebaseUser(bearer) ?: throw DefaultUnauthorizedException(null)
        val user = userService.updateCurrentUser(currentUser.id, reqBody)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun deleteCurrentUser(request: ServerRequest): ServerResponse {
        val bearer =
            request.headers().firstHeader(HttpHeaders.AUTHORIZATION) ?: throw DefaultUnauthorizedException(null)
        val currentUser = userService.getFirebaseUser(bearer) ?: throw DefaultUnauthorizedException(null)
        userService.deleteFirebaseUser(bearer)
        userService.deleteCurrentUser(currentUser.id)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun uploadCurrentUserData(request: ServerRequest): ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val currentUser = AuthUserHolder.awaitUser()
        val fieldType = try {
            UploadType.valueOf((reqData[PART_NAME_FIELD]?.firstOrNull() as? FormFieldPart)?.value() ?: "")
        } catch (e: Exception) {
            throw DefaultBadRequestException(null)
        }
        return when (fieldType) {
            UploadType.AVATAR -> {
                val avatar = (reqData[PART_FILES_FIELD]?.firstOrNull() as? FilePart) ?: throw DefaultBadRequestException(Exception(""))
                val fileExtension = avatar.filename().split(".").last()
                val res = googleStorageService.uploadFile(
                    path = "users/${currentUser.id}/avatar.$fileExtension",
                    contentType = "image/$fileExtension",
                    data = avatar.content().asFlow().map {
                        val buffer = ByteBuffer.allocate(it.capacity())
                        it.toByteBuffer(buffer)
                        buffer
                    },
                )
                userService.updateCurrentUser(currentUser.id, UpdateCurrentUserRequest(
                    avatar = res.path
                ))
                ServerResponse
                    .ok()
                    .bodyValueAndAwait(
                        Response(data = res)
                    )
            }
            else -> ServerResponse
                .badRequest()
                .buildAndAwait()
        }
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
}