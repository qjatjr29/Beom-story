package com.beomsic.userservice.adapter.out.oauth

import com.beomsic.userservice.application.port.out.OAuthPort
import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.domain.oauth.SocialType
import com.beomsic.userservice.infrastructure.oauth.OAuth2Properties
import com.beomsic.userservice.infrastructure.oauth.OAuth2Property
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuthClient(
    private val oauth2Properties: OAuth2Properties,
    private val oAuth2TokenClient: OAuthTokenClient,
    private val oAuth2UserInfoClient: OAuthUserInfoClient
): OAuthPort {

    override suspend fun getAuthCodeUri(socialType: SocialType): String {
        val property: OAuth2Property = getProperty(socialType)

        return UriComponentsBuilder.fromUriString(property.provider.authorizationUri)
            .queryParam("client_id", property.registration.clientId)
            .queryParam("redirect_uri", property.registration.redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", property.registration.scope)
            .build()
            .toUriString()
    }

    override suspend fun getUserInfo(socialType: SocialType, code: String): OAuthUserInfo {
        val property: OAuth2Property = getProperty(socialType)
        val accessToken = oAuth2TokenClient.fetchToken(property = property, socialType = socialType, code = code)
        return oAuth2UserInfoClient.fetchUserInfo(
            userInfoUrl = property.provider.userInfoUri,
            socialType = socialType,
            accessToken = accessToken)
    }

    private fun getProperty(socialType: SocialType): OAuth2Property {
        return oauth2Properties.getOauth2Properties()[socialType]
            ?: throw IllegalArgumentException("Invalid social type: $socialType")
    }
}