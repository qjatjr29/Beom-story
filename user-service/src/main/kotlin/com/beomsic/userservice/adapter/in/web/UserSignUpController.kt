package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.adapter.`in`.web.dto.SignUpRequest
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user-service")
class UserSignUpController (
    private val userSignUpUseCase: UserSignUpUseCase,
) {

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody request: SignUpRequest) {

        val command = UserSignUpCommand(request.email, request.password, request.nickname)

        userSignUpUseCase.execute(command)
    }

}