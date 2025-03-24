package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.application.port.out.UserLoginPort
import com.beomsic.userservice.infrastructure.jwt.JwtTokenProvider
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class UserLoginAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
    private val jwtTokenProvider: JwtTokenProvider
): UserLoginPort {

    override suspend fun login(userId: Long, email: String): String {
        redisTemplate.delete("logoutlist:$userId")
        val token = jwtTokenProvider.createToken(userId, email)

        redisTemplate.opsForValue()
            .set(
                "refreshToken:$userId",
                token.refreshToken,
                jwtTokenProvider.getRefreshTokenExpiresTime(),
                TimeUnit.MILLISECONDS)

        return token.accessToken
    }

    override suspend fun reissue(refreshToken: String): String {

        // Refresh Token으로 Redis에서 User ID 조회
        val userId = jwtTokenProvider.getUserId(refreshToken)

        // Redis에 저장된 Refresh Token과 비교
        val storedRefreshToken = redisTemplate.opsForValue()
            .get("refreshToken:$userId")

        if (storedRefreshToken.isNullOrEmpty() || storedRefreshToken != refreshToken) {
            throw IllegalArgumentException("Refresh Token이 유효하지 않거나 일치하지 않습니다.")
        }

        // 유효한 경우 새로운 Access Token 발급
        val email = jwtTokenProvider.getEmail(refreshToken)
        val token = jwtTokenProvider.createToken(userId, email)

        redisTemplate.opsForValue()
            .set(
                "refreshToken:$userId",
                token.refreshToken,
                jwtTokenProvider.getRefreshTokenExpiresTime(),
                TimeUnit.MILLISECONDS)

        return token.accessToken
    }

    // todo: Auth
    override suspend fun logout(userId: Long, accessToken: String) {
        val expiration = jwtTokenProvider.getAccessTokenExpiresTime()
        redisTemplate.opsForValue()
            .set("logoutlist:$accessToken", "true", expiration, TimeUnit.MILLISECONDS)

        redisTemplate.delete("refreshToken:$userId")
    }

}