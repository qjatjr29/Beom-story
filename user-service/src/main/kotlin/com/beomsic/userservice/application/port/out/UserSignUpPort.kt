package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand

interface UserSignUpPort {
    suspend fun signup(command: UserSignUpCommand)
}