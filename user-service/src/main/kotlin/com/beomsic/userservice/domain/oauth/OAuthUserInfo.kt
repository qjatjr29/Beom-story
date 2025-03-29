package com.beomsic.userservice.domain.oauth

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
sealed interface OAuthUserInfo {
    val provider: String
    val providerId: String
    val email: String
    val name: String
    val profileImage: String?
}

data class GoogleUserInfo(
    @JsonProperty("sub") override val providerId: String,
    override val email: String,
    override val name: String,
    @JsonProperty("picture") override val profileImage: String? = null,
) : OAuthUserInfo {
    override val provider: String = "GOOGLE"
}

data class KakaoUserInfo(
    @JsonProperty("id") override val providerId: String,
    val kakaoAccount: KakaoAccount
) : OAuthUserInfo {
    override val email: String = kakaoAccount.email
    override val name: String = kakaoAccount.profile.nickname
    override val profileImage: String? = kakaoAccount.profile.profileImageUrl
    override val provider: String = "KAKAO"

    data class KakaoAccount(
        val email: String,
        val profile: KakaoProfile
    )

    data class KakaoProfile(
        val nickname: String,
        @JsonProperty("profile_image_url") val profileImageUrl: String?
    )
}