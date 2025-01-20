package com.beomsic.userservice.application.port.`in`.command

data class UserLoginCommand(
    val email: String,
    val password: String,
)