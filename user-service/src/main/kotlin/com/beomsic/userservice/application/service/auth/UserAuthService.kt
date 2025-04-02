package com.beomsic.userservice.application.service.auth

import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserAuthUseCase
import com.beomsic.userservice.application.port.out.UserAuthPort
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.service.dto.UserDto
import com.beomsic.userservice.domain.exception.PasswordNotMatchedException
import com.beomsic.userservice.domain.exception.PasswordNotSetException
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import org.springframework.stereotype.Service

@Service
class UserAuthService(
    private val userFindPort: UserFindPort,
    private val userAuthPort: UserAuthPort
): UserAuthUseCase {

    override suspend fun login(command: UserLoginCommand): UserDto {
        val user = userFindPort.findByEmail(command.email)

        if (user.password == null) throw PasswordNotSetException()

        if (!BCryptUtils.verify(command.password, user.password)) {
            throw PasswordNotMatchedException()
        }

        val accessToken = userAuthPort.login(userId = user.id, email = user.email)
        return UserDto(user, accessToken)
    }

    override suspend fun logout(userId: Long,  accessToken: String) {
        userFindPort.findById(userId)
        userAuthPort.logout(userId, accessToken)
    }

    override suspend fun reissueToken(refreshToken: String): String {
        return userAuthPort.reissue(refreshToken)
    }
}