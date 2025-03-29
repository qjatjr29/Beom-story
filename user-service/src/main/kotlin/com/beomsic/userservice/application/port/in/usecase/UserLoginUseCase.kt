package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand

interface UserLoginUseCase {
    suspend fun login(command: UserLoginCommand) : String
    suspend fun logout(userId: Long, accessToken: String)
    suspend fun reissueToken(refreshToken: String): String
}