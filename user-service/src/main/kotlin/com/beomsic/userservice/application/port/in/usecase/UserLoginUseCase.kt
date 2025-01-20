package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.adapter.`in`.web.UserLoginResponse
import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand

interface UserLoginUseCase {
    suspend fun login(command: UserLoginCommand) : UserLoginResponse
    suspend fun logout(userId: Long, accessToken: String)
    suspend fun reissueToken(refreshToken: String): String
}