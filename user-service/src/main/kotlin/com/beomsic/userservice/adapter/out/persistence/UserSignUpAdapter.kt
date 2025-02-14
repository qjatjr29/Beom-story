package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.domain.util.BCryptUtils
import org.springframework.stereotype.Component

@Component
class UserSignUpAdapter(
    private val userRepository: UserRepository,
) : UserSignUpPort {

    override suspend fun signup(command: UserSignUpCommand) {
        val userEntity = UserEntity(
            email = command.email,
            password = BCryptUtils.hash(command.password),
            nickname = command.nickname)

        userRepository.save(userEntity)
    }
}