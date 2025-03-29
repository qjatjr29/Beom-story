package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.application.service.dto.UserDto

interface OAuthUserCase {
    suspend fun getAuthCodeUri(provider: String): String
    suspend fun login(provider: String, code: String): UserDto
}