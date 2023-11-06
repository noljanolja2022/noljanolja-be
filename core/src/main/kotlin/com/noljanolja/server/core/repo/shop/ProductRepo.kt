package com.noljanolja.server.core.repo.shop

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepo: CoroutineCrudRepository<ProductModel, String> {
    fun findAllBy(
        pageable: Pageable,
    ): Flow<ProductModel>

    fun findByNameContaining(
        query: String, pageable: Pageable,
    ): Flow<ProductModel>

    fun findByIsActive(
        isActive: Boolean, pageable: Pageable,
    ): Flow<ProductModel>

    fun findByNameContainingAndIsActive(
        query: String, isActive: Boolean, pageable: Pageable,
    ): Flow<ProductModel>

    suspend fun countByIsActive(isActive: Boolean = true): Long
}