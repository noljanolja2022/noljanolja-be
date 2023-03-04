package com.noljanolja.server.core.service

import com.noljanolja.server.core.model.CoreUser
import com.noljanolja.server.core.model.Gender
import com.noljanolja.server.core.model.request.UpsertUserRequest
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.repo.user.toUser
import org.springframework.stereotype.Component

@Component
class UserDS(
    private val userRepo: UserRepo,
) {
    suspend fun getUserByFirebaseUserId(firebaseUserId: String): CoreUser? {
        return userRepo.findByFirebaseUserId(firebaseUserId)?.toUser()
    }

    suspend fun getUsers(page: Long, pageSize: Long): Pair<List<CoreUser>, Long> {
        val totalRecords = userRepo.count()
        return Pair(
            userRepo.findAllUsers(
                offset = (page - 1) * pageSize,
                limit = pageSize,
            ).map { it.toUser() },
            totalRecords
        )
    }

    suspend fun upsertUser(request: UpsertUserRequest): CoreUser {
        var user = userRepo.findByFirebaseUserId(request.firebaseUserId)
        if (user == null) {
            user = com.noljanolja.server.core.repo.user.UserModel(
                firebaseUserId = request.firebaseUserId,
            )
            user.isNewRecord = true
        }
        user.apply {
            request.name?.let { name = it }
            request.phone?.let { phone = it }
            email = request.email
            gender = request.gender ?: Gender.Other
            avatar = request.profileImage ?: ""
            pushToken = request.pushToken
        }
        val res = userRepo.save(user)
        return res.toUser()
    }
}