package com.beomsic.userservice.adapter.out.persistence.adapter

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.adapter.out.persistence.UserRepository
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.domain.exception.UserEmailAlreadyException
import com.beomsic.userservice.domain.model.User
import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import org.springframework.stereotype.Component

@Component
class UserSignUpAdapter(
    private val userRepository: UserRepository,
) : UserSignUpPort {

    override suspend fun signup(command: UserSignUpCommand): User {

        if (userRepository.existsByEmail(command.email)) throw UserEmailAlreadyException()

        val userEntity = UserEntity(
            email = command.email,
            password = BCryptUtils.hash(command.password),
            nickname = command.nickname)

        val user = userRepository.save(userEntity)
        return user.toDomain()
    }

    override suspend fun oauthSignup(userInfo: OAuthUserInfo): User {

        if (userRepository.existsByEmail(userInfo.email)) throw UserEmailAlreadyException()

        val userEntity = UserEntity(
            email = userInfo.email,
            nickname = userInfo.name,
            provider = userInfo.provider,
            providerId = userInfo.providerId,
            profileUrl = userInfo.profileImage
        )
        val user = userRepository.save(userEntity)
        return user.toDomain()
    }
}