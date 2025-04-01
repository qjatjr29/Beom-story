package com.beomsic.userservice.application.service.dto

import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.domain.model.User
import java.time.LocalDateTime

data class UserDto(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileUrl: String? = null,
    val authType: AuthType,
    val accessToken: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        operator fun invoke(user: User, accessToken: String? = null) = with(user) {
            UserDto(
                id = id,
                profileUrl = if (profileUrl.isNullOrEmpty()) null else "",
                nickname = nickname,
                email = email,
                authType = authType,
                accessToken = accessToken,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}