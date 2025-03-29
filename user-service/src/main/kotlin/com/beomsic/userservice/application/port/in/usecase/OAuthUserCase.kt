package com.beomsic.userservice.application.port.`in`.usecase

interface OAuthUserCase {
    suspend fun getAuthCodeUri(provider: String): String
    suspend fun login(provider: String, code: String): String
}