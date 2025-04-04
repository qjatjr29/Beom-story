package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import com.beomsic.userservice.adapter.`in`.web.dto.UserNicknameUpdateRequest
import com.beomsic.userservice.adapter.`in`.web.dto.UserPasswordUpdateRequest
import com.beomsic.userservice.application.port.`in`.usecase.UserUpdateUseCase
import com.beomsic.userservice.domain.exception.AuthenticationException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.PasswordNotMatchedException
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.infrastructure.config.AuthTokenResolver
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserUpdateControllerTest {
    private lateinit var webTestClient: WebTestClient

    @MockK
    private lateinit var userUpdateUseCase: UserUpdateUseCase

    @InjectMockKs
    private lateinit var userUpdateController: UserUpdateController

    @BeforeEach
    fun setUp() {
        val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()
        webTestClient = WebTestClient
            .bindToController(userUpdateController)
            .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
            .controllerAdvice(globalExceptionHandler)
            .configureClient()
            .build()
        MockKAnnotations.init(this, relaxUnitFun = true)
        clearMocks(userUpdateUseCase)
    }

    @Nested
    inner class `닉네임 업데이트 테스트` {
        @Test
        fun `닉네임 업데이트 요청 정상 동작`() = runTest {
            // given
            val userId = 1L
            val accessToken = "access-token"
            val newNickname = "newNickname"
            val email = "user@example.com"
            val request = UserNicknameUpdateRequest(newNickname)

            coEvery { userUpdateUseCase.updateUserNickname(any()) } just Runs

            // when & then
            webTestClient.patch().uri("/user-service/$userId/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNoContent

            coVerify {
                userUpdateUseCase.updateUserNickname(
                    withArg { command ->
                        assertThat(command.userId).isEqualTo(userId)
                        assertThat(command.authUserId).isEqualTo(userId)
                        assertThat(command.nickName).isEqualTo(newNickname)
                    }
                )
            }
        }

        @Test
        fun `권한이 없는 유저가 닉네임 업데이트 요청시 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 2L
            val accessToken = "access-token"
            val newNickname = "newNickname"
            val email = "user@example.com"
            val request = UserNicknameUpdateRequest(newNickname)

            coEvery { userUpdateUseCase.updateUserNickname(any()) } throws AuthenticationException()

            // when & then
            webTestClient.patch().uri("/user-service/$userId/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header("userId", authUserId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.message).isEqualTo(AuthenticationException().message)
                    assertThat(response.code).isEqualTo(403)
                }
        }

        @Test
        fun `없는 유저의 닉네임 업데이트 요청시 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 2L
            val accessToken = "access-token"
            val newNickname = "newNickname"
            val email = "user@example.com"
            val request = UserNicknameUpdateRequest(newNickname)

            coEvery { userUpdateUseCase.updateUserNickname(any()) } throws UserNotFoundException()

            // when & then
            webTestClient.patch().uri("/user-service/$userId/nickname")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header("userId", authUserId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.message).isEqualTo(UserNotFoundException().message)
                    assertThat(response.code).isEqualTo(404)
                }
        }

    }

    @Nested
    inner class `비밀번호 업데이트 테스트` {
        @Test
        fun `비밀번호 업데이트 요청 정상 동작`() = runTest {
            // given
            val userId = 1L
            val email = "user@example.com"
            val currentPassword = "oldPassword123"
            val newPassword = "newPassword456!"
            val request = UserPasswordUpdateRequest(currentPassword, newPassword)

            coEvery { userUpdateUseCase.updateUserPassword(any()) } just Runs

            // when & then
            webTestClient.patch().uri("/user-service/$userId/password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNoContent

            coVerify {
                userUpdateUseCase.updateUserPassword(
                    withArg { command ->
                        assertThat(command.userId).isEqualTo(userId)
                        assertThat(command.authUserId).isEqualTo(userId)
                        assertThat(command.currentPassword).isEqualTo(currentPassword)
                        assertThat(command.newPassword).isEqualTo(newPassword)
                    }
                )
            }
        }

        @Test
        fun `없는 유저의 비밀번호 업데이트 요청시 예외 반환`() = runTest {
            // given
            val userId = 1L
            val email = "user@example.com"
            val currentPassword = "oldPassword123"
            val newPassword = "newPassword456!"
            val request = UserPasswordUpdateRequest(currentPassword, newPassword)

            coEvery { userUpdateUseCase.updateUserPassword(any()) } throws UserNotFoundException()

            // when & then
            webTestClient.patch().uri("/user-service/$userId/password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.message).isEqualTo(UserNotFoundException().message)
                    assertThat(response.code).isEqualTo(404)
                }
        }

        @Test
        fun `잘못된 비밀번호 형식으로 비밀번호 업데이트 요청시 예외 반환`() = runTest {
            // given
            val userId = 1L
            val email = "user@example.com"
            val currentPassword = "oldPassword123"
            val newPassword = "newPassword456"
            val request = UserPasswordUpdateRequest(currentPassword, newPassword)

            coEvery { userUpdateUseCase.updateUserPassword(any()) } throws InvalidPasswordException()

            // when & then
            webTestClient.patch().uri("/user-service/$userId/password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.message).isEqualTo(InvalidPasswordException().message)
                    assertThat(response.code).isEqualTo(400)
                }
        }

        @Test
        fun `기존 비밀번호를 틀렸을 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val email = "user@example.com"
            val currentPassword = "oldPassword123"
            val newPassword = "newPassword456"
            val request = UserPasswordUpdateRequest(currentPassword, newPassword)

            coEvery { userUpdateUseCase.updateUserPassword(any()) } throws PasswordNotMatchedException()

            // when & then
            webTestClient.patch().uri("/user-service/$userId/password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .header("userId", userId.toString())
                .header("email", email)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.message).isEqualTo(PasswordNotMatchedException().message)
                    assertThat(response.code).isEqualTo(400)
                }
        }
    }

    @Nested
    inner class `유저 삭제 테스트` {
        @Test
        fun `유저 삭제 요청 정상 동작`() = runTest {

            // given
            val userId = 1L

            coEvery { userUpdateUseCase.deleteUser(any<Long>(), any<Long>()) } just Runs

            // when
            webTestClient.delete().uri("/user-service/$userId/withdrawal")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .header("userId", userId.toString())
                .header("email", "user@example.com")
                .exchange()
                .expectStatus().isNoContent

            // then
        }

        @Test
        fun `권한 없는 유저가 삭제 요청시 예외 반환`() = runTest {

            // given
            val userId = 1L
            val authUserId = 2L

            coEvery { userUpdateUseCase.deleteUser(any<Long>(), any<Long>()) } throws AuthenticationException()

            // when
            webTestClient.delete().uri("/user-service/$userId/withdrawal")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .header("userId",authUserId.toString())
                .header("email", "user@example.com")
                .exchange()
                .expectStatus().isForbidden
                .expectBody(ErrorResponse::class.java)
                .value { response ->
                    assertThat(response.message).isEqualTo(AuthenticationException().message)
                    assertThat(response.code).isEqualTo(403)
                }

            // then
        }

    }

}