package com.beomsic.userservice.domain.oauth

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
sealed interface OAuthTokenResponse {
    val accessToken: String
    val tokenType: String
    val expiresIn: Int
    val refreshToken: String?
    val scope: String
}

data class GoogleTokenResponse(
    override val accessToken: String,
    override val tokenType: String,
    override val expiresIn: Int,
    override val refreshToken: String?,
    override val scope: String
): OAuthTokenResponse

data class KakaoTokenResponse(
    override val accessToken: String,
    override val tokenType: String,
    override val expiresIn: Int,
    override val refreshToken: String?,
    val refreshTokenExpiresIn: Int,
    override val scope: String
): OAuthTokenResponse