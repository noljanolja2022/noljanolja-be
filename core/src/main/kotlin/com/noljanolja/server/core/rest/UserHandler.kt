package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.model.Pagination
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserContact
import com.noljanolja.server.core.model.UserPreferences
import com.noljanolja.server.core.rest.request.UpdateUserContactsRequest
import com.noljanolja.server.core.rest.request.UpsertUserRequest
import com.noljanolja.server.core.rest.response.GetUsersResponseData
import com.noljanolja.server.core.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class UserHandler(
    private val userService: UserService,
) {
    companion object {
        private const val DEFAULT_PAGE = 1
        private const val DEFAULT_PAGE_SIZE = 10
    }

    suspend fun getUsers(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val friendId = request.queryParamOrNull("friendId")
        val (users, total) = userService.getUsers(
            page = page,
            pageSize = pageSize,
            friendId = friendId,
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = GetUsersResponseData(
                        users = users,
                        pagination = Pagination(page, pageSize, total)
                    )
                )
            )
    }

    suspend fun getUserDetails(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val user = userService.getUser(userId) ?: throw UserNotFound
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = user,
                )
            )
    }

    suspend fun upsertUser(request: ServerRequest): ServerResponse {
        val upsertUserRequest = request.awaitBodyOrNull<UpsertUserRequest>() ?: throw RequestBodyRequired
        val existingUser = userService.getUser(upsertUserRequest.id)
        val upsertUser = existingUser?.copy(
            // TODO only upsert non null / blank value from upsertUserRequest:
            name = upsertUserRequest.name?.takeIf { it.isNotBlank() } ?: existingUser.name
        ) ?: User(
            id = upsertUserRequest.id,
            name = upsertUserRequest.name.orEmpty(),
            avatar = upsertUserRequest.avatar,
            // TODO new user should have phone
            phone = upsertUserRequest.phone.orEmpty(),
            email = upsertUserRequest.email,
            dob = upsertUserRequest.dob,
            gender = upsertUserRequest.gender,
            preferences = upsertUserRequest.preferences ?: UserPreferences(),
        )
        val user = userService.upsertUser(
            upsertUser
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = user,
                )
            )
    }

    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        userService.deleteUser(userId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun upsertUserContacts(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val updateUserContactsRequest = request.awaitBodyOrNull<UpdateUserContactsRequest>()
            ?: throw RequestBodyRequired
        userService.upsertUserContacts(userId, updateUserContactsRequest.contacts.flatMap { localContact ->
            mutableListOf<UserContact>().apply {
                addAll(localContact.emails.distinct().map { email ->
                    UserContact(
                        id = 0,
                        name = localContact.name,
                        phone = null,
                        email = email,
                    )
                })
                addAll(localContact.phones.distinct().map { phone ->
                    UserContact(
                        id = 0,
                        name = localContact.name,
                        phone = phone,
                        email = null,
                    )
                })
            }
        })
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response<Nothing>())
    }
}