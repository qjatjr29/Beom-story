package com.beomsic.userservice.application.port.`in`.command

data class UserSignUpCommand(
    val email: String,
    val password: String,
    val username: String,
)
