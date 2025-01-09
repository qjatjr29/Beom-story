package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.port.`in`.usecase.SignUpUserUseCase
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.domain.exception.UserExistsException
import org.springframework.stereotype.Service

@Service
class UserSignUpService(
    private val userFindPort: UserFindPort,
    private val userSignUpPort : UserSignUpPort,
    private val validationService: ValidationService,
) : SignUpUserUseCase {

    override suspend fun execute(command: UserSignUpCommand) {
        with(command) {

            validationService.validateEmail(email)
            validationService.validatePassword(password)

            userFindPort.findByEmail(email)?.let {
                throw UserExistsException()
            }

            userSignUpPort.signup(this)
        }
    }
}