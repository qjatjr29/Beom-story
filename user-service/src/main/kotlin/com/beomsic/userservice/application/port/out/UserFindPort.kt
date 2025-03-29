package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.adapter.out.persistence.UserEntity

interface UserFindPort {
    suspend fun findById(id: Long) : UserEntity
    suspend fun findByEmail(email: String) : UserEntity?
    suspend fun findByProviderAndProviderId(provider: String, providerId: String) : UserEntity?
}