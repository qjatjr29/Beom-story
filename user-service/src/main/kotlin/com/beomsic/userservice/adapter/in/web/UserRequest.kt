package com.beomsic.userservice.adapter.`in`.web

data class SignUpRequest (
    val email: String,
    val password: String,
    val username: String,
    val profileUrl: String? = null,
)

data class LoginRequest (
    val email: String,
    val password: String,
)