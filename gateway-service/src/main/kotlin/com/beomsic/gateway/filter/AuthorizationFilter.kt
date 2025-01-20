package com.beomsic.gateway.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthorizationFilter(
    @Value("\${jwt.secret}") secretToken: String,
    private val redisTemplate: RedisTemplate<String, String>,
) : AbstractGatewayFilterFactory<AuthorizationFilter.Config>(Config::class.java) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secretToken)
    private val logger = KotlinLogging.logger {}

    override fun apply(config: Config?): GatewayFilter = GatewayFilter { exchange, chain ->

        val request = exchange.request
        val authHeader = request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()
        val token = authHeader?.removePrefix("Bearer ") ?: return@GatewayFilter chain.filter(exchange)

        if (isLogoutedUser(token)) {
            return@GatewayFilter onError(exchange, "로그아웃된 토큰입니다.", HttpStatus.UNAUTHORIZED)
        }

        if (!isValidToken(token)) {
            return@GatewayFilter onError(exchange, "토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED)
        }

        val userId = getUserId(token)
        val email = getEmail(token)

        val modifiedRequest = request.mutate()
            .header("userId", userId.toString())
            .header("email", email)
            .build()

        chain.filter(exchange.mutate().request(modifiedRequest).build())
    }

    private fun isLogoutedUser(token: String): Boolean {
        val key = "logoutlist:${token}"
        return redisTemplate.hasKey(key)
    }

    private fun isValidToken(token: String): Boolean {
        return try {
            val decodedJWT: DecodedJWT = JWT.require(algorithm)
                .acceptExpiresAt(0)
                .build()
                .verify(token)
            !decodedJWT.subject.isNullOrEmpty()
        } catch (ex: Exception) {
            false
        }
    }

    private fun onError(exchange: ServerWebExchange, error: String, status: HttpStatus): Mono<Void> {
        logger.error(error)
        exchange.response.statusCode = status
        return exchange.response.setComplete()
    }

    private fun getUserId(jwt: String): Long {
        val decodedJWT: DecodedJWT = JWT.require(algorithm)
            .build()
            .verify(jwt)
        return decodedJWT.getClaim("userId").asLong()
    }

    private fun getEmail(jwt: String): String {
        val decodedJWT: DecodedJWT = JWT.require(algorithm)
            .build()
            .verify(jwt)
        return decodedJWT.getClaim("email").asString()
    }

    class Config
}