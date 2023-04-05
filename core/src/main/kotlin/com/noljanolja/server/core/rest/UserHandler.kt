package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserContact
import com.noljanolja.server.core.model.UserPreferences
import com.noljanolja.server.core.rest.request.UpsertUserContactsRequest
import com.noljanolja.server.core.rest.request.UpsertUserRequest
import com.noljanolja.server.core.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets

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
        var phoneNumber = request.queryParamOrNull("phoneNumber")
        if (!phoneNumber.isNullOrBlank()) {
            phoneNumber = UriUtils.decode(phoneNumber, StandardCharsets.UTF_8)
        }
        val (users, total) = userService.getUsers(
            page = page,
            pageSize = pageSize,
            friendId = friendId,
            phoneNumber = phoneNumber
        )
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = users,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total
                    ),
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
            name = upsertUserRequest.name?.takeIf { it.isNotBlank() } ?: existingUser.name,
            email = upsertUserRequest.email?.takeIf { it.isNotBlank() } ?: existingUser.email,
            avatar = upsertUserRequest.avatar?.takeIf { it.isNotBlank() } ?: existingUser.avatar,
            dob = upsertUserRequest.dob ?: existingUser.dob,
            gender = upsertUserRequest.gender ?: existingUser.gender,
            preferences = upsertUserRequest.preferences ?: existingUser.preferences
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
        val user = userService.upsertUser(upsertUser, existingUser == null)
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
        val upsertUserContactsRequest = request.awaitBodyOrNull<UpsertUserContactsRequest>()
            ?: throw RequestBodyRequired
        val updatedUserIds =
            userService.upsertUserContacts(userId, upsertUserContactsRequest.contacts.flatMap { localContact ->
                localContact.phones.mapNotNull { phone ->
                    UserContact(
                        id = 0,
                        name = localContact.name,
                        phone = phone,
                    ).takeIf { phone.isNotBlank() }
                }
            }.distinctBy { it.phone.orEmpty() })
        val users = userService.getUsersByIds(updatedUserIds)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = users))
    }
}