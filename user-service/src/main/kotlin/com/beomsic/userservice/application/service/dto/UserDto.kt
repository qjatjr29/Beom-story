package com.beomsic.userservice.application.service.dto

import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val accessToken: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)