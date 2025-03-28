package com.beomsic.userservice.adapter.out.persistence.adapter

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.adapter.out.persistence.UserRepository
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import org.springframework.stereotype.Component

@Component
class UserSignUpAdapter(
    private val userRepository: UserRepository,
) : UserSignUpPort {

    override suspend fun signup(command: UserSignUpCommand): UserEntity {
        val userEntity = UserEntity(
            email = command.email,
            password = BCryptUtils.hash(command.password),
            nickname = command.nickname)

        return userRepository.save(userEntity)
    }

    override suspend fun oauthSignup(userInfo: OAuthUserInfo): UserEntity {
        val userEntity = UserEntity(
            email = userInfo.email,
            nickname = userInfo.name,
            provider = userInfo.provider,
            providerId = userInfo.providerId,
            profileUrl = userInfo.profileImage
        )
        return userRepository.save(userEntity)
    }
}