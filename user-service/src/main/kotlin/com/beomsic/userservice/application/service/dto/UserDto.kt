package com.beomsic.userservice.application.service.dto

data class UserDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val accessToken: String?,
)