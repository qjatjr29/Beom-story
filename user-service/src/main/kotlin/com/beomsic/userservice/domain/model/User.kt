package com.beomsic.userservice.domain.model

import java.time.LocalDateTime

data class User (
    val id: Long? = null,
    val email: String,
    val nickname: String,
    val profileUrl: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)