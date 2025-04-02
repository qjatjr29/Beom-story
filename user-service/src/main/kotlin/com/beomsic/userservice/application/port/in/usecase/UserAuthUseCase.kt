package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.service.dto.UserDto

interface UserAuthUseCase {
    suspend fun login(command: UserLoginCommand): UserDto
    suspend fun logout(userId: Long, accessToken: String)
    suspend fun reissueToken(refreshToken: String): String
}