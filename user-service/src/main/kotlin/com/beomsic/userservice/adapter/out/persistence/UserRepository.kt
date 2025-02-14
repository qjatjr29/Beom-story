package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.domain.exception.UserNotFoundException
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<UserEntity, Long> {
    suspend fun findByEmail(email: String) : UserEntity?
    suspend fun existsByEmail(email: String): Boolean
    suspend fun existsByNickname(nickname: String): Boolean
}

suspend fun UserRepository.findByIdOrNull(id: Long): UserEntity {
    return findById(id) ?: throw UserNotFoundException()
}