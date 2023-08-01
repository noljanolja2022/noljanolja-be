package com.noljanolja.server.reward.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReferralRewardConfigRepo : CoroutineCrudRepository<ReferralRewardConfigModel, Long> {
}