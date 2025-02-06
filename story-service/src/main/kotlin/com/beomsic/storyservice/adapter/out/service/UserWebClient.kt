package com.beomsic.storyservice.adapter.out.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class UserWebClient(
    webClientBuilder: WebClient.Builder,
    @Value("\${external.uri.user-service}")
    private val baseUrl: String
) {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    suspend fun findById(userId: Long): UserResponse {
        return webClient.get()
            .uri("/$userId")
            .retrieve()
            .bodyToMono(UserResponse::class.java)
            .awaitSingle()
    }
}