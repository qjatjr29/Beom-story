package com.beomsic.userservice.adapter.out.persistence.adapter

import com.beomsic.userservice.adapter.out.persistence.UserRepository
import com.beomsic.userservice.adapter.out.persistence.findByEmailOrNull
import com.beomsic.userservice.adapter.out.persistence.findByIdOrNull
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserFindAdapter (
    private val userRepository: UserRepository
) : UserFindPort {

    override suspend fun findById(id: Long): User {
        return userRepository.findByIdOrNull(id).toDomain()
    }

    override suspend fun findByEmail(email: String): User {
        return userRepository.findByEmailOrNull(email).toDomain()
    }

    override suspend fun findByProviderAndProviderId(provider: String, providerId: String): User? {
        return userRepository.findByProviderAndProviderId(provider, providerId)?.toDomain()
    }
}