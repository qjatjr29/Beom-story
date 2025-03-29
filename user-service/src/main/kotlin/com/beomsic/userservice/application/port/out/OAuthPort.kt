package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.domain.oauth.SocialType

interface OAuthPort {
    suspend fun getAuthCodeUri(socialType: SocialType): String
    suspend fun getUserInfo(socialType: SocialType, code: String): OAuthUserInfo
}