package com.beomsic.userservice.adapter.out.oauth

import com.beomsic.userservice.domain.oauth.SocialType
import com.beomsic.userservice.infrastructure.oauth.OAuth2Property
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Component
class OAuthTokenClient(
    private val webClientBuilder: WebClient.Builder,
) {

    suspend fun fetchToken(property: OAuth2Property, socialType: SocialType, code: String): String {
        val webClient = webClientBuilder.baseUrl(property.provider.tokenUri).build()

        val response = webClient.post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData("client_id", property.registration.clientId)
                    .with("client_secret", property.registration.clientSecret)
                    .with("code", code)
                    .with("grant_type", "authorization_code")
                    .with("redirect_uri", property.registration.redirectUri)
            )
            .retrieve()
            .bodyToMono(socialType.oauthTokenResponse)
            .awaitSingle()

        return response.accessToken
    }
}