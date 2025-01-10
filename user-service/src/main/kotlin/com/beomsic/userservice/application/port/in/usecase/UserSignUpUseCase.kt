package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand

interface UserSignUpUseCase {
    suspend fun execute(command: UserSignUpCommand)
}