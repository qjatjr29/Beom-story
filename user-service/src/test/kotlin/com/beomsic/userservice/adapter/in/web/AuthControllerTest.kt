package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import com.beomsic.userservice.adapter.`in`.web.dto.LoginRequest
import com.beomsic.userservice.adapter.`in`.web.dto.UserLoginResponse
import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserAuthUseCase
import com.beomsic.userservice.application.service.dto.UserDto
import com.beomsic.userservice.domain.exception.InvalidJwtTokenException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.PasswordNotSetException
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.infrastructure.config.AuthTokenResolver
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime

@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class AuthControllerTest {
    private lateinit var webTestClient: WebTestClient

    @MockK
    private lateinit var userAuthUseCase: UserAuthUseCase

    @InjectMockKs
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()
        webTestClient = WebTestClient
            .bindToController(authController)
            .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
            .controllerAdvice(globalExceptionHandler)
            .configureClient()
            .build()
    }

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        clearMocks(userAuthUseCase)
    }

    @Nested
    inner class `로그인 테스트` {

        @Test
        fun `로그인 요청 정상 동작`() {
            // given
            val userId = 1L
            val email = "user@example.com"
            val password = "password123!"
            val accessToken = "accessToken"

            val request = LoginRequest(email = email, password = password)
            val command = UserLoginCommand(email = email, password = password)
            val userDto = UserDto(
                id = userId,
                email = email,
                nickname = "testUser",
                authType = AuthType.EMAIL_PASSWORD,
                accessToken = accessToken,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

            coEvery { userAuthUseCase.login(command) } returns userDto

            // when
            webTestClient.post()
                .uri("/user-service/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody(UserLoginResponse::class.java)
                .value { response ->
                    assertThat(response.id).isEqualTo(userId)
                    assertThat(response.email).isEqualTo(email)
                    assertThat(response.accessToken).isEqualTo(accessToken)
                }

            // then
            coVerify(exactly = 1) { userAuthUseCase.login(command) }
        }

        @Test
        fun `회원가입하지 않은 이메일로 로그인 요청시 예외 반환`() {
            // given
            val email = "nonUser@example.com"
            val password = "password123!"

            val request = LoginRequest(email = email, password = password)
            val command = UserLoginCommand(email = email, password = password)
            coEvery { userAuthUseCase.login(command) } throws UserNotFoundException()

            // when
            webTestClient.post()
                .uri("/user-service/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(404)
                    assertThat(response.message).isEqualTo(UserNotFoundException().message)
                }

            // then
            coVerify(exactly = 1) { userAuthUseCase.login(command) }
        }

        @Test
        fun `password가 설정되지 않은 유저(소셜 로그인)의 경우 email_password로 로그인 요청시 예외 반환`() {
            // given
            val email = "user@example.com"
            val password = "password123!"

            val request = LoginRequest(email = email, password = password)
            val command = UserLoginCommand(email = email, password = password)
            coEvery { userAuthUseCase.login(command) } throws PasswordNotSetException()

            // when
            webTestClient.post()
                .uri("/user-service/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(400)
                    assertThat(response.message).isEqualTo(PasswordNotSetException().message)
                }

            // then
            coVerify(exactly = 1) { userAuthUseCase.login(command) }
        }

        @Test
        fun `잘못된 비밀번호로 로그인 요청시 예외 반환`() {
            // given
            val email = "user@example.com"
            val invalidPassword = "invalidPwd123!"

            val request = LoginRequest(email = email, password = invalidPassword)
            val command = UserLoginCommand(email = email, password = invalidPassword)
            coEvery { userAuthUseCase.login(command) } throws InvalidPasswordException()

            // when
            webTestClient.post()
                .uri("/user-service/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(400)
                    assertThat(response.message).isEqualTo(InvalidPasswordException().message)
                }

            // then
            coVerify(exactly = 1) { userAuthUseCase.login(command) }
        }

        @Test
        fun `로그인 과정에서 예상치 못한 문제 발생 시 예외 반환`() {
            // given
            val email = "user@example.com"
            val password = "password123!"

            val request = LoginRequest(email = email, password = password)
            val command = UserLoginCommand(email = email, password = password)

            coEvery { userAuthUseCase.login(command) } throws RuntimeException()

            // when
            webTestClient.post()
                .uri("/user-service/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(500)
                }

            // then
            coVerify(exactly = 1) { userAuthUseCase.login(command) }
        }

    }

    @Nested
    inner class `로그아웃 테스트` {

        @Test
        fun `로그아웃 요청 정상 동작`() {
            // given
            val accessToken = "accessToken"
            val userId = 1L
            val email = "user@example.com"

            coEvery { userAuthUseCase.logout(userId, accessToken) } just Runs

            // when
            webTestClient.delete().uri("/user-service/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .exchange()
                .expectStatus().isNoContent

            // then
            coVerify { userAuthUseCase.logout(userId, accessToken) }
        }

        @Test
        fun `없는 유저에 대한 로그아웃 요청시 예외 반환`() {
            // given
            val accessToken = "accessToken"
            val userId = 1L
            val email = "user@example.com"

            coEvery { userAuthUseCase.logout(userId, accessToken) } throws UserNotFoundException()

            // when
            webTestClient.delete().uri("/user-service/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(404)
                    assertThat(response.message).isEqualTo(UserNotFoundException().message)
                }

            // then
            coVerify { userAuthUseCase.logout(userId, accessToken) }
        }

        @Test
        fun `로그아웃 과정에서의 예상치 못한 문제 발생시 예외 반환`() {
            // given
            val accessToken = "accessToken"
            val userId = 1L
            val email = "user@example.com"

            coEvery { userAuthUseCase.logout(userId, accessToken) } throws RuntimeException()

            // when
            webTestClient.delete().uri("/user-service/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .exchange()
                .expectStatus().is5xxServerError
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(500)
                }

            // then
            coVerify { userAuthUseCase.logout(userId, accessToken) }
        }

    }

    @Nested
    inner class `토큰 갱신 테스트` {
        @Test
        fun `토큰 갱신 요청 정상 동작`() {
            // given
            val refreshToken = "refreshToken"
            val newAccessToken = "newAccessToken"

            coEvery { userAuthUseCase.reissueToken(refreshToken) } returns newAccessToken

            // when
            webTestClient.post()
                .uri(
                    UriComponentsBuilder
                    .fromPath("/user-service/token/reissue")
                    .queryParam("refreshToken", refreshToken)
                    .toUriString()
                )
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .value { response -> assertThat(response).isEqualTo(newAccessToken) }
            // then
        }

        @Test
        fun `refresh token이 유효하지 않은 경우 예외 반환`() {
            // given
            val invalidRefreshToken = "invalidRefreshToken"

            coEvery { userAuthUseCase.reissueToken(invalidRefreshToken) } throws InvalidJwtTokenException()

            // when
            webTestClient.post()
                .uri(
                    UriComponentsBuilder
                        .fromPath("/user-service/token/reissue")
                        .queryParam("refreshToken", invalidRefreshToken)
                        .toUriString()
                )
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(400)
                    assertThat(response.message).isEqualTo(InvalidJwtTokenException().message)
                }
            // then
        }

        @Test
        fun `갱신 과정에서 예상치 못한 문제 발생시 예외 반환`() {
            // given
            val refreshToken = "refreshToken"

            coEvery { userAuthUseCase.reissueToken(refreshToken) } throws RuntimeException()

            // when
            webTestClient.post()
                .uri(
                    UriComponentsBuilder
                        .fromPath("/user-service/token/reissue")
                        .queryParam("refreshToken", refreshToken)
                        .toUriString()
                )
                .exchange()
                .expectStatus().is5xxServerError
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.code).isEqualTo(500)
                }
            // then
        }
    }

}