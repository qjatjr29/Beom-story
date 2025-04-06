package com.beomsic.placeservice.infra.config

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthTokenResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthToken::class.java)
    }

    override fun resolveArgument(parameter: MethodParameter,
                                 bindingContext: BindingContext, exchange: ServerWebExchange): Mono<Any> {
        val authHeader = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()
        val token = authHeader?.removePrefix("Bearer ") ?: throw IllegalArgumentException()
        val userId = exchange.request.headers["userId"]?.firstOrNull()
        val email = exchange.request.headers["email"]?.firstOrNull()

        checkNotNull(userId) { "userId header is missing" }
        checkNotNull(email) { "email header is missing" }

        return Mono.just(
            AuthUser(
            id = userId.toLong(),
            email = email,
            accessToken = token, null)
        )
    }
}
