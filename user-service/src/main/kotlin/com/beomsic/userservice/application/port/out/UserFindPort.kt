package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.infrastructure.persistence.UserEntity

interface UserFindPort {
    suspend fun findById(id: Long) : UserEntity
    suspend fun findByEmail(email: String) : UserEntity?
}