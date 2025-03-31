package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.domain.model.User
import com.beomsic.userservice.domain.oauth.OAuthUserInfo

interface UserSignUpPort {
    suspend fun signup(command: UserSignUpCommand): User
    suspend fun oauthSignup(userInfo: OAuthUserInfo): User
}