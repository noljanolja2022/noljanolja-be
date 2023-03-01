package com.noljanolja.server.core.service

import com.noljanolja.server.common.model.request.UpsertUserRequest
import com.noljanolja.server.core.repo.user.UserRepo
import com.noljanolja.server.core.repo.user.toUser
import com.noljanolja.server.common.model.CoreUser
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
        val user = (userRepo.findByFirebaseUserId(request.firebaseUserId) ?: com.noljanolja.server.core.repo.user.UserModel(
            firebaseUserId = request.firebaseUserId,
        ).apply { isNewRecord = true }).apply {
            name = request.name
            profileImage = request.profileImage
            pushToken = request.pushToken
            pushNotiEnabled = request.pushNotiEnabled
        }
        userRepo.save(user)
        return user.toUser()
    }
}