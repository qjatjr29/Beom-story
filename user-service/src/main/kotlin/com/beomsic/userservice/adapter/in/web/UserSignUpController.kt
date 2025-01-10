package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-service")
class UserSignUpController (
    private val userSignUpUseCase: UserSignUpUseCase,
) {

    @PostMapping("/signup")
    suspend fun signup(@RequestBody request: SignUpRequest) {

        val command = UserSignUpCommand(
            request.email,
            request.password,
            request.username
        )

        userSignUpUseCase.execute(command)
    }

}