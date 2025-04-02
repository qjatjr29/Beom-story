package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.adapter.out.persistence.adapter.UserAuthAdapter
import com.beomsic.userservice.application.service.auth.JwtTokenProvider
import com.beomsic.userservice.domain.exception.InvalidJwtTokenException
import com.beomsic.userservice.domain.model.Token
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserAuthAdapterTest {

    @MockK
    lateinit var redisTemplate: RedisTemplate<String, String>

    @MockK
    lateinit var jwtTokenProvider: JwtTokenProvider

    @InjectMockKs
    lateinit var userAuthAdapter: UserAuthAdapter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(redisTemplate, jwtTokenProvider)
    }

    @Test
    fun `로그인 정상 동작`() = runTest {
        // given
        val userId = 1L
        val email = "test@test.com"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val refreshTokenExpiresTime = 100L
        val token = Token(accessToken, refreshToken)

        every { redisTemplate.delete(any<String>()) } returns true
        every { jwtTokenProvider.createToken(userId, email) } returns token
        every { jwtTokenProvider.getRefreshTokenExpiresTime() } returns refreshTokenExpiresTime
        coEvery { redisTemplate.opsForValue()
            .set(any<String>(), refreshToken, refreshTokenExpiresTime, TimeUnit.MILLISECONDS) } just Runs

        // when
        val result = userAuthAdapter.login(userId, email)

        // then
        assertThat(result).isEqualTo(accessToken)
        coVerify(exactly = 1) { redisTemplate.delete(any<String>()) }
        coVerify(exactly = 1) { jwtTokenProvider.createToken(userId, email) }
        coVerify(exactly = 1) { redisTemplate.opsForValue()
            .set(any<String>(), refreshToken, refreshTokenExpiresTime, TimeUnit.MILLISECONDS) }
    }

    @Test
    fun `redis 예외 발생 시 로그인 예외 처리`() = runTest {
        // given
        val userId = 1L
        val email = "user@example.com"
        coEvery { redisTemplate.delete(any<String>()) } throws RuntimeException("Redis 오류 발생")

        // when & then
        assertThrows<RuntimeException> {
            userAuthAdapter.login(userId, email)
        }
        coVerify(exactly = 1) { redisTemplate.delete(any<String>()) }
    }

    @Test
    fun `토큰 재발급 정상 동작`() = runTest {
        // given
        val userId = 1L
        val email = "user@example.com"
        val oldRefreshToken = "oldRefreshToken"
        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"
        val refreshTokenExpiresTime = 100L
        val token = Token(newAccessToken, newRefreshToken)

        coEvery { jwtTokenProvider.getUserId(oldRefreshToken) } returns userId
        coEvery { redisTemplate.opsForValue().get("refreshToken:$userId") } returns oldRefreshToken
        coEvery { jwtTokenProvider.getEmail(oldRefreshToken) } returns email
        coEvery { jwtTokenProvider.createToken(userId, email) } returns token
        coEvery { jwtTokenProvider.getRefreshTokenExpiresTime() } returns refreshTokenExpiresTime
        coEvery { redisTemplate.opsForValue()
            .set("refreshToken:$userId", newRefreshToken, refreshTokenExpiresTime, TimeUnit.MILLISECONDS) } just Runs

        // when
        val result = userAuthAdapter.reissue(oldRefreshToken)

        // then
        assertThat(result).isEqualTo(newAccessToken)
        coVerify(exactly = 1) { jwtTokenProvider.getUserId(oldRefreshToken) }
        coVerify(exactly = 1) { jwtTokenProvider.getEmail(oldRefreshToken) }
        coVerify(exactly = 1) { jwtTokenProvider.createToken(userId, email) }
    }

    @Test
    fun `저장된 Refresh Token이 없는 경우에 토큰 재발급시 예외 발생`() = runTest {
        // given
        val userId = 1L
        val invalidRefreshToken = "invalidRefreshToken"

        coEvery { jwtTokenProvider.getUserId(invalidRefreshToken) } returns userId
        coEvery { redisTemplate.opsForValue().get("refreshToken:$userId") } returns null

        // when & then
        assertThrows<InvalidJwtTokenException> {
            runBlocking { userAuthAdapter.reissue(invalidRefreshToken) }
        }
    }

    @Test
    fun `로그아웃 - 정상 동작`() = runTest {
        // given
        val userId = 1L
        val accessToken = "accessToken"
        val expiration = 1000L

        coEvery { jwtTokenProvider.getAccessTokenExpiresTime() } returns expiration
        coEvery { redisTemplate.opsForValue()
            .set("logoutlist:$accessToken", "true", expiration, TimeUnit.MILLISECONDS) } just Runs
        coEvery { redisTemplate.delete("refreshToken:$userId") } returns true

        // when
        userAuthAdapter.logout(userId, accessToken)

        // then
        coVerify(exactly = 1) { jwtTokenProvider.getAccessTokenExpiresTime() }
        coVerify(exactly = 1) { redisTemplate.opsForValue()
            .set("logoutlist:$accessToken", "true", expiration, TimeUnit.MILLISECONDS) }
        coVerify(exactly = 1) { redisTemplate.delete("refreshToken:$userId") }
    }

}