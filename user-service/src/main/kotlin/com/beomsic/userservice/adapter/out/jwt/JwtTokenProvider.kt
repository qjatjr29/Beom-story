package com.beomsic.userservice.adapter.out.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.beomsic.userservice.domain.model.Token
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secretToken: String,
    private val properties: JwtProperties
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secretToken)

    fun createToken(userId: Long, email: String): Token {
        val accessToken = createJwtToken(userId, email, properties.accessExpiresTime)
        val refreshToken = createJwtToken(userId, email, properties.refreshExpiresTime)

        return Token(accessToken, refreshToken)
    }

    fun getAccessTokenExpiresTime(): Long = properties.accessExpiresTime
    fun getRefreshTokenExpiresTime(): Long = properties.refreshExpiresTime

    fun getUserId(jwt: String): Long {
        val decodedJWT: DecodedJWT = JWT.require(algorithm)
            .build()
            .verify(jwt)
        return decodedJWT.getClaim("userId").asLong()
    }

    fun getEmail(jwt: String): String {
        val decodedJWT: DecodedJWT = JWT.require(algorithm)
            .build()
            .verify(jwt)
        return decodedJWT.getClaim("email").asString()
    }

    private fun createJwtToken(userId: Long, email: String, expiresTime: Long): String =
        JWT.create()
            .withIssuer(properties.issuer)
            .withSubject(properties.subject)
            .withIssuedAt(Date())
            .withExpiresAt(Date(Date().time + expiresTime))
            .withClaim("userId", userId)
            .withClaim("email", email)
            .sign(Algorithm.HMAC256(properties.secret))

}