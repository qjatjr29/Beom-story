package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.domain.model.Token

interface UserLoginPort {
    suspend fun login(userId: Long, email: String) : Token
    suspend fun logout(userId: Long, accessToken: String)
    suspend fun reissue(refreshToken: String): String
}