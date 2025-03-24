package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.domain.model.User

interface UserFindUseCase {
    suspend fun findById(id: Long): User
}