package com.beomsic.userservice.domain.oauth

import java.util.*

enum class SocialType(
    val oauthTokenResponse: Class<out OAuthTokenResponse>,
    val userInfo: Class<out OAuthUserInfo>
) {
    KAKAO(KakaoTokenResponse::class.java, KakaoUserInfo::class.java),
    GOOGLE(GoogleTokenResponse::class.java, GoogleUserInfo::class.java);
//    NAVER

    companion object {
        fun fromProvider(provider: String): SocialType {
            return entries
                .find { it.name == provider.uppercase(Locale.ENGLISH) }
                ?: throw IllegalArgumentException("No matching SocialType for provider: $provider")
        }
    }
}
