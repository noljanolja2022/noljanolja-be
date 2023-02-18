package com.noljanolja.server.core.service

import com.noljanolja.server.common.model.request.UpsertUserRequest
import com.noljanolja.server.common.model.CoreUser as UserModel
import org.springframework.stereotype.Component

@Component
class UserDS(
    private val userRepo: UserRepo,
) {
    suspend fun getUserByFirebaseUserId(firebaseUserId: String): UserModel? {
        return userRepo.findByFirebaseUserId(firebaseUserId)?.toUserModel()
    }

    suspend fun getUsers(page: Long, pageSize: Long): Pair<List<UserModel>, Long> {
        val totalRecords = userRepo.count()
        return Pair(
            userRepo.findAllUsers(
                offset = (page - 1) * pageSize,
                limit = pageSize,
            ).map { it.toUserModel() },
            totalRecords
        )
    }

    suspend fun upsertUser(request: UpsertUserRequest): UserModel {
        val user = (userRepo.findByFirebaseUserId(request.firebaseUserId) ?: User(
            firebaseUserId = request.firebaseUserId,
        ).apply { isNewRecord = true }).apply {
            name = request.name
            profileImage = request.profileImage
            pushToken = request.pushToken
            pushNotiEnabled = request.pushNotiEnabled
        }
        userRepo.save(user)
        return user.toUserModel()
    }
}