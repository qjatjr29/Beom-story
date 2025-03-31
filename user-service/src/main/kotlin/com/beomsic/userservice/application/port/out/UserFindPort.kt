package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.domain.model.User

interface UserFindPort {
    suspend fun findById(id: Long) : User
    suspend fun findByEmail(email: String) : User
    suspend fun findByProviderAndProviderId(provider: String, providerId: String) : User?
}