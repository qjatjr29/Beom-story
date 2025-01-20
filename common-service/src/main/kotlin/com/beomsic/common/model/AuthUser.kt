package com.beomsic.common.model

data class AuthUser(
    val id: Long,
    val email: String,
    val accessToken: String,
    val refreshToken: String?
)
