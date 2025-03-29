package com.beomsic.userservice.adapter.out.oauth

import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.domain.oauth.SocialType
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class OAuthUserInfoClient(
    private val webClientBuilder: WebClient.Builder,
) {
    suspend fun fetchUserInfo(userInfoUrl: String, socialType: SocialType, accessToken: String): OAuthUserInfo {
        val webClient = webClientBuilder.baseUrl(userInfoUrl).build()
        return webClient.get()
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(socialType.userInfo)
            .awaitSingle()
    }
}