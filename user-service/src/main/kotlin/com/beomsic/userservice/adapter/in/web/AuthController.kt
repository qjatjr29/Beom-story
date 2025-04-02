package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.userservice.adapter.`in`.web.dto.LoginRequest
import com.beomsic.userservice.adapter.`in`.web.dto.SignUpRequest
import com.beomsic.userservice.adapter.`in`.web.dto.UserLoginResponse
import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserAuthUseCase
import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user-service")
class AuthController(
    private val userSignUpUseCase: UserSignUpUseCase,
    private val userAuthUseCase: UserAuthUseCase,
) {

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody request: SignUpRequest) {

        val command = UserSignUpCommand(request.email, request.password, request.nickname)
        userSignUpUseCase.execute(command)
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody request: LoginRequest): UserLoginResponse {
        val userLoginCommand = UserLoginCommand(
            email = request.email,
            password = request.password
        )

        val userDto = userAuthUseCase.login(userLoginCommand)
        return UserLoginResponse(id = userDto.id,
            email = userDto.email,
            nickname = userDto.nickname,
            accessToken = userDto.accessToken)
    }

    @PostMapping("/token/reissue")
    suspend fun refreshToken(@RequestParam("refreshToken") refreshToken: String): String =
        userAuthUseCase.reissueToken(refreshToken)

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken authUser: AuthUser) =
        userAuthUseCase.logout(authUser.id, authUser.accessToken)
}