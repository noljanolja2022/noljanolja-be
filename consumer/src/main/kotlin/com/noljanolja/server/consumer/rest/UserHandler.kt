package com.noljanolja.server.consumer.rest

import com.noljanolja.server.common.exception.DefaultUnauthorizedException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.rest.request.SyncUserContactsRequest
import com.noljanolja.server.consumer.rest.request.UpdateCurrentUserRequest
import com.noljanolja.server.consumer.service.UserService
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class UserHandler(
    private val userService: UserService,
) {

    suspend fun getCurrentUser(request: ServerRequest): ServerResponse {
        var currentUser = AuthUserHolder.awaitUser()
        if (currentUser == null) {
            val bearer = request.headers().firstHeader(HttpHeaders.AUTHORIZATION) ?: throw DefaultUnauthorizedException(null)
            currentUser = userService.getFirebaseUser(bearer) ?: throw DefaultUnauthorizedException(null)
        }
        val user = userService.getCurrentUser(currentUser) ?: throw DefaultUnauthorizedException(null)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun updateCurrentUser(request: ServerRequest): ServerResponse {
        val reqBody = request.awaitBodyOrNull<UpdateCurrentUserRequest>() ?: throw RequestBodyRequired
        val bearer = request.headers().firstHeader(HttpHeaders.AUTHORIZATION) ?: throw DefaultUnauthorizedException(null)
        val currentUser = userService.getFirebaseUser(bearer) ?: throw DefaultUnauthorizedException(null)
        val user = userService.updateCurrentUser(currentUser.id, reqBody)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(data = user)
            )
    }

    suspend fun deleteCurrentUser(request: ServerRequest): ServerResponse {
        val bearer = request.headers().firstHeader(HttpHeaders.AUTHORIZATION) ?: throw DefaultUnauthorizedException(null)
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
        // TODO logic
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response<Nothing>()
            )
    }

    suspend fun syncCurrentUserContact(request: ServerRequest): ServerResponse {
        val currentUserId = AuthUserHolder.awaitUser()?.id ?: throw DefaultUnauthorizedException(null)
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