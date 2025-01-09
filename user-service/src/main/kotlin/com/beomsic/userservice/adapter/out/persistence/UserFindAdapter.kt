package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.application.port.out.UserFindPort
import org.springframework.stereotype.Component

@Component
class UserFindAdapter (
    private val userRepository: UserRepository
) : UserFindPort {
    override suspend fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }
}