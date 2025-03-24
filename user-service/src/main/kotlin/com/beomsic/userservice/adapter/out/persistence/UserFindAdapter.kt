package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.infrastructure.persistence.UserEntity
import com.beomsic.userservice.infrastructure.persistence.UserRepository
import com.beomsic.userservice.infrastructure.persistence.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserFindAdapter (
    private val userRepository: UserRepository
) : UserFindPort {

    override suspend fun findById(id: Long): UserEntity {
        return userRepository.findByIdOrNull(id)
    }

    override suspend fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }
}