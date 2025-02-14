package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.application.port.out.CheckDuplicatePort
import org.springframework.stereotype.Component

@Component
class CheckDuplicateAdapter(
    private val userRepository: UserRepository
): CheckDuplicatePort {
    override suspend fun isDuplicatedEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override suspend fun isDuplicatedNickname(nickname: String): Boolean {
        return userRepository.existsByNickname(nickname)
    }
}