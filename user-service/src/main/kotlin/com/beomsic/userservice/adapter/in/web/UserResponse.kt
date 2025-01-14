package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.domain.User
import java.time.LocalDateTime

data class UserDetailResponse (
    val id: Long,
    val email: String,
    val username: String,
    val profileUrl : String?,
    val createdAt : LocalDateTime?,
    val updatedAt : LocalDateTime?,
) {
    companion object {
        operator fun invoke(user: User) = with(user) {
            UserDetailResponse(
                id = id!!,
                profileUrl = if (profileUrl.isNullOrEmpty()) null else "",
                username = username,
                email = email,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }
}