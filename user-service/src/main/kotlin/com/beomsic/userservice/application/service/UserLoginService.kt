package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserLoginUseCase
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.port.out.UserLoginPort
import com.beomsic.userservice.application.service.dto.UserDto
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.PasswordNotMatchedException
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import org.springframework.stereotype.Service

@Service
class UserLoginService(
    private val userFindPort: UserFindPort,
    private val userLoginPort: UserLoginPort
): UserLoginUseCase {

    override suspend fun login(command: UserLoginCommand): UserDto {
        val user = userFindPort.findByEmail(command.email)

        if (user.password == null) throw InvalidPasswordException("비밀번호가 설정되지 않았습니다!!")

        if (!BCryptUtils.verify(command.password, user.password)) {
            throw PasswordNotMatchedException()
        }

        val accessToken = userLoginPort.login(userId = user.id, email = user.email)
        return UserDto(user.id, user.email, user.nickname, accessToken)
    }

    override suspend fun reissueToken(refreshToken: String): String {
        return userLoginPort.reissue(refreshToken)
    }

    override suspend fun logout(userId: Long, accessToken: String) {
        userFindPort.findById(userId)
        userLoginPort.logout(userId, accessToken)
    }
}