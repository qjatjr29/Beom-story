package com.beomsic.userservice.infrastructure.config

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.userservice.domain.exception.InvalidJwtTokenException
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
        // 여기에 작성한 조건이 동작하는 경우에만 이 리졸버가 동작
        // 리플렉션을 사용해 파라미터로 들어온 파라미터를 사용해
        // AuthToken이 존재하는 경우, 즉 컨트롤러의 함수에 @AuthToken 어노테이션이 존재하는 경우
        // 이 경우에 supportParameter 값이 true가 되어 스프링 내부에서 해당 리졸브 argument가 동작
        return parameter.hasParameterAnnotation(AuthToken::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        // supportsParameter가 true인 경우 동작하게 되는 상세 구현
        // 요청 헤더 값에 Authorization 값이 존재할 경우 첫 번째 값을 리턴

        val authHeader = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()
        val token = authHeader?.removePrefix("Bearer ") ?: throw InvalidJwtTokenException()

        // "userId"와 "username"을 헤더에서 읽어오는 부분
        val userId = exchange.request.headers["userId"]?.firstOrNull()
        val email = exchange.request.headers["email"]?.firstOrNull()

        // 값이 없으면 예외 처리
        checkNotNull(userId) { "userId header is missing" }
        checkNotNull(email) { "email header is missing" }

        return Mono.just(AuthUser(
            id = userId.toLong(),
            email = email,
            accessToken = token, null))
    }
}
