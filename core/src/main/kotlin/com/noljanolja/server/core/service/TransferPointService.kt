package com.noljanolja.server.core.service

import com.noljanolja.server.common.utils.REASON_RECEIVE_POINT
import com.noljanolja.server.common.utils.REASON_SEND_POINT
import com.noljanolja.server.core.model.UserTransferPoint
import com.noljanolja.server.core.repo.pointtransfer.TransferPointRepo
import com.noljanolja.server.core.repo.pointtransfer.UserTransferPointModel
import com.noljanolja.server.core.repo.pointtransfer.toUserTransferPoint
import com.noljanolja.server.loyalty.exception.Error
import com.noljanolja.server.loyalty.repo.MemberInfoModel
import com.noljanolja.server.loyalty.repo.MemberInfoRepo
import com.noljanolja.server.loyalty.repo.TransactionModel
import com.noljanolja.server.loyalty.repo.TransactionRepo
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class TransferPointService (
    private val transferPointRepo: TransferPointRepo,
    private val memberInfoRepo: MemberInfoRepo,
    private val transactionRepo: TransactionRepo
) {
    suspend fun requestPoint(
        fromUserId: String,
        toUserId: String,
        points: Long
    ): UserTransferPoint {
        return transferPointRepo.save(
            UserTransferPointModel(
                fromUserId = fromUserId,
                toUserId = toUserId,
                points = points,
                type = UserTransferPoint.Type.REQUEST
            )
        ).toUserTransferPoint()
    }

    suspend fun sendPoint(
        fromUserId: String,
        toUserId: String,
        points: Long
    ) : UserTransferPoint {
        //Verify and Update sender's points
        val senderInfo = memberInfoRepo.findById(fromUserId)
            ?: throw Error.MemberNotFound
        if (senderInfo.availablePoints < points) {
            throw Error.InsufficientPointBalance
        }

        memberInfoRepo.save(
            MemberInfoModel(
                memberId = fromUserId,
                accumulatedPoints = senderInfo.accumulatedPoints,
                availablePoints = senderInfo.availablePoints - points
            )
        )

        transactionRepo.save(
            TransactionModel(
                memberId = fromUserId,
                amount = -points,
                reason = REASON_SEND_POINT,
                log = null
            )
        )

        //Update receiver's points
        val receiverInfo = memberInfoRepo.findById(toUserId)
            ?: throw Error.MemberNotFound

        memberInfoRepo.save(
            MemberInfoModel(
                memberId = toUserId,
                accumulatedPoints = receiverInfo.accumulatedPoints + points,
                availablePoints = receiverInfo.availablePoints + points
            )
        )

        transactionRepo.save(
            TransactionModel(
                memberId = toUserId,
                amount = points,
                reason = REASON_RECEIVE_POINT,
                log = null
            )
        )

        //Save user transfer point
        return transferPointRepo.save(
            UserTransferPointModel(
                fromUserId = fromUserId,
                toUserId = toUserId,
                points = points,
                type = UserTransferPoint.Type.SEND
            )
        ).toUserTransferPoint()
    }
}