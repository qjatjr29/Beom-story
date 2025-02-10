package com.beomsic.placeservice.adapter.out.external.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono


@Component
class ImageWebClient(
    webClientBuilder: WebClient.Builder,
    @Value("\${external.uri.image-service}")
    private val baseUrl: String
) {

    private val webClient = webClientBuilder.baseUrl(baseUrl).build()

    suspend fun uploadImage(image: FilePart): String {

        val body = MultipartBodyBuilder().apply {
//            part("placeId", placeId)
            part("image", image)
        }.build()

        return webClient.post()
            .uri("/upload")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(body))
            .retrieve()
            .bodyToMono<String>()
            .awaitSingle()
    }
}