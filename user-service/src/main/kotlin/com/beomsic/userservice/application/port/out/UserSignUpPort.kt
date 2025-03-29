package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.domain.oauth.OAuthUserInfo

interface UserSignUpPort {
    suspend fun signup(command: UserSignUpCommand): UserEntity
    suspend fun oauthSignup(userInfo: OAuthUserInfo): UserEntity
}