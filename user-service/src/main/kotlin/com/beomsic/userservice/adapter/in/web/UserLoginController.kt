package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserLoginUseCase
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user-service")
class UserLoginController(
    private val userLoginUseCase: UserLoginUseCase,
    private val redisTemplate: RedisTemplate<String, String>,
) {

    @PostMapping("/login")
    suspend fun login(@RequestBody request: LoginRequest) : UserLoginResponse {
        val userLoginCommand = UserLoginCommand(
            email = request.email,
            password = request.password
        )

        return userLoginUseCase.login(userLoginCommand)
    }

    @PostMapping("/token/reissue")
    suspend fun refreshToken(@RequestParam("refreshToken") refreshToken: String): String =
        userLoginUseCase.reissueToken(refreshToken)


    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken authUser: AuthUser) {
        userLoginUseCase.logout(authUser.id, authUser.accessToken)
    }

}