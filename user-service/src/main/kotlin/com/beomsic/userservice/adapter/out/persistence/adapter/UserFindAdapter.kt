package com.beomsic.userservice.adapter.out.persistence.adapter

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.adapter.out.persistence.UserRepository
import com.beomsic.userservice.adapter.out.persistence.findByIdOrNull
import com.beomsic.userservice.application.port.out.UserFindPort
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

    override suspend fun findByProviderAndProviderId(provider: String, providerId: String): UserEntity? {
        return userRepository.findByProviderAndProviderId(provider, providerId)
    }
}