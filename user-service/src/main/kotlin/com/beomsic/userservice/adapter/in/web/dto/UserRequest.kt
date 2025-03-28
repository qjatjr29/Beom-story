package com.beomsic.userservice.adapter.`in`.web.dto

data class SignUpRequest (
    val email: String,
    val password: String,
    val nickname: String,
    val profileUrl: String? = null,
)

data class LoginRequest (
    val email: String,
    val password: String,
)