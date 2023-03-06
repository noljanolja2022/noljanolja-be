package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserContact
import com.noljanolja.server.core.repo.user.UserContactsRepo
import com.noljanolja.server.core.repo.user.UserModel.Companion.toUserModel
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.repo.user.toUser
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Component

@Component
class UserService(
    private val userRepo: UserRepo,
    private val userContactsRepo: UserContactsRepo,
    private val contactsRepo: UserContactsRepo,
    private val objectMapper: ObjectMapper,
) {

    suspend fun getUsers(
        page: Int,
        pageSize: Int,
    ): Pair<List<User>, Int> = coroutineScope {
        val total = async {
            userRepo.count()
        }
        val users = async {
            userRepo.findAll(
                offset = ((page - 1) * pageSize),
                limit = pageSize,
            ).map { it.toUser(objectMapper) }
        }
        Pair(
            users.await(),
            total.await().toInt(),
        )
    }

    suspend fun getUser(
        userId: String,
    ): User? = userRepo.findById(userId)?.toUser(objectMapper)

    suspend fun upsertUser(
        user: User
    ): User = userRepo.save(user.toUserModel(objectMapper)).toUser(objectMapper)

    suspend fun deleteUser(
        userId: String,
    ) {
        userRepo.softDeleteById(userId)
    }

    suspend fun getUserContacts(
        userId: String,
    ): List<UserContact> {
        // TODO get and map user contact
    }

    suspend fun upsertUserContacts(
        userId: String,
        userContacts: List<UserContact>
    ) {
        // TODO update user contacts
    }
}