package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserContact
import com.noljanolja.server.core.model.UserPreferences
import com.noljanolja.server.core.rest.request.*
import com.noljanolja.server.core.service.UserService
import com.noljanolja.server.core.utils.genReferralCode
import com.noljanolja.server.loyalty.service.LoyaltyService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets

@Component
class UserHandler(
    private val userService: UserService,
    private val loyaltyService: LoyaltyService,
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
        var query = request.queryParamOrNull("query")
        if (!query.isNullOrBlank()) {
            query = UriUtils.decode(query, StandardCharsets.UTF_8)
        }
        if (!phoneNumber.isNullOrBlank()) {
            phoneNumber = UriUtils.decode(phoneNumber, StandardCharsets.UTF_8)
        }
        val (users, total) = userService.getUsers(
            page = page,
            pageSize = pageSize,
            friendId = friendId,
            phoneNumber = phoneNumber,
            query = query,
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

    suspend fun updateUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId")
        val req = request.awaitBodyOrNull<UpdateUserRequest>() ?: throw RequestBodyRequired
        val existingUser = userService.getUser(userId) ?: throw UserNotFound
        val updatedUser = userService.upsertUser(existingUser.copy(
            name = req.name?.takeIf { it.isNotBlank() } ?: existingUser.name,
            email = req.email?.takeIf { it.isNotBlank() } ?: existingUser.email,
            avatar = req.avatar?.takeIf { it.isNotBlank() } ?: existingUser.avatar,
            dob = req.dob ?: existingUser.dob,
            gender = req.gender ?: existingUser.gender,
            isActive = req.isActive ?: existingUser.isActive
        ))
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                body = Response(
                    data = updatedUser,
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
            referralCode = genReferralCode(),
        )
        val user = userService.upsertUser(upsertUser, existingUser == null)
        if (existingUser == null) {
            loyaltyService.upsertMember(user.id)
        }
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

    suspend fun sendFriendRequest(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val reqBody = request.awaitBodyOrNull<AddFriendRequest>()
            ?: throw RequestBodyRequired
        userService.addFriendRequest(userId, reqBody.friendId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun userBlockUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        with(request.awaitBodyOrNull<UserBlockUserRequest>() ?: throw RequestBodyRequired) {
            userService.userBlockUser(
                userId = userId,
                blockedUserId = blockedUserId,
                isBlocked = isBlocked,
            )
        }
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getBlackListOfUser(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_PAGE_SIZE
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val (blockedUsers, total) = userService.getBlackListOfUser(
            page = page,
            pageSize = pageSize,
            userId = userId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = blockedUsers,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun assignReferral(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<AssignReferralRequest>() ?: throw RequestBodyRequired
        val userId = request.pathVariable("userId").ifBlank { throw InvalidParamsException("userId") }
        val rewardPoints = userService.assignReferral(
            userId = userId,
            referredByCode = payload.referredByCode,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = rewardPoints,
                )
            )
    }
}