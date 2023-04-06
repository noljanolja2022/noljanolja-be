package com.noljanolja.server.loyalty.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberInfoRepo: CoroutineCrudRepository<MemberInfoModel, String> {

}