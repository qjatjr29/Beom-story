package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.application.port.`in`.usecase.OAuthUserCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/user-service")
class OAuthController(
    private val oauthUserCase: OAuthUserCase,
) {

    @GetMapping("/oauth2/authorize/{provider}")
    suspend fun login(@PathVariable("provider") provider: String, response: ServerHttpResponse){
        val redirectUrl = oauthUserCase.getAuthCodeUri(provider)
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(redirectUrl)
    }

    @GetMapping("/login/oauth2/code/{provider}")
    suspend fun callback(@PathVariable provider: String, @RequestParam code: String): ResponseEntity<String> {
        val userInfo = oauthUserCase.login(provider, code)
        return ResponseEntity.ok().body(userInfo)
    }

}