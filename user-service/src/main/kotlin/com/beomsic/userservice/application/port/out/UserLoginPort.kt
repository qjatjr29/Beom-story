package com.beomsic.userservice.application.port.out

interface UserLoginPort {
    suspend fun login(userId: Long, email: String) : String
    suspend fun logout(userId: Long, accessToken: String)
    suspend fun reissue(refreshToken: String): String
}