package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import com.beomsic.userservice.application.port.out.UserSignUpPort
import org.springframework.stereotype.Service

@Service
class UserSignUpService(
    private val userSignUpPort : UserSignUpPort,
    private val validationService: ValidationService,
) : UserSignUpUseCase {

    override suspend fun execute(command: UserSignUpCommand) {
        with(command) {
            validationService.validateEmail(email)
            validationService.validatePassword(password)
            userSignUpPort.signup(this)
        }
    }
}