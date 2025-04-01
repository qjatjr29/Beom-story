package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.application.service.dto.UserDto

interface UserFindUseCase {
    suspend fun findById(id: Long): UserDto
}