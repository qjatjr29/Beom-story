package com.beomsic.userservice.adapter.`in`.web.dto

import com.beomsic.userservice.application.service.dto.UserDto
import java.time.LocalDateTime

data class UserDetailResponse (
    val id: Long,
    val email: String,
    val nickname: String,
    val profileUrl : String?,
    val authType: String,
    val createdAt : LocalDateTime?,
    val updatedAt : LocalDateTime?,
) {
    companion object {
        operator fun invoke(user: UserDto) = with(user) {
            UserDetailResponse(
                id = id,
                profileUrl = if (profileUrl.isNullOrEmpty()) null else "",
                nickname = nickname,
                email = email,
                authType = authType.name,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}

data class UserLoginResponse (
    val id: Long,
    val email: String,
    val nickname: String,
    val accessToken: String?,
)

data class CheckDuplicateResponse (
    val isDuplicated: Boolean
)