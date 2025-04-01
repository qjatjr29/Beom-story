package com.beomsic.userservice.application.service.auth

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.application.service.ValidationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSignUpService(
    private val userSignUpPort : UserSignUpPort,
    private val validationService: ValidationService,
) : UserSignUpUseCase {

    @Transactional
    override suspend fun execute(command: UserSignUpCommand) {
        with(command) {
            validationService.validateEmail(email)
            validationService.validatePassword(password)
            userSignUpPort.signup(this)
        }
    }
}