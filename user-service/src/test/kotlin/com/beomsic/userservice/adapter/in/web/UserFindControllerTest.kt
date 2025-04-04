package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import com.beomsic.userservice.adapter.`in`.web.dto.UserDetailResponse
import com.beomsic.userservice.application.port.`in`.usecase.UserFindUseCase
import com.beomsic.userservice.application.service.dto.UserDto
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.infrastructure.config.AuthTokenResolver
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserFindControllerTest {

    private lateinit var webTestClient: WebTestClient

    @MockK
    private lateinit var userFindUseCase: UserFindUseCase

    @InjectMockKs
    private lateinit var userFindController: UserFindController

    @BeforeEach
    fun setUp() {
        val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()
        webTestClient = WebTestClient
            .bindToController(userFindController)
            .argumentResolvers { it.addCustomResolver(AuthTokenResolver()) }
            .controllerAdvice(globalExceptionHandler)
            .configureClient()
            .build()
        MockKAnnotations.init(this, relaxUnitFun = true)
        clearMocks(userFindUseCase)
    }

    @Test
    fun `id를 통한 유저 조회 요청 정상 동작`() = runTest {
        // given
        val userId = 1L
        val email = "user@example.com"
        val nickname = "testUser"
        val userDto = UserDto(
            id = userId,
            email = email,
            nickname = nickname,
            authType = AuthType.EMAIL_PASSWORD,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        coEvery { userFindUseCase.findById(userId) } returns userDto

        // when
        webTestClient.get()
            .uri("/user-service/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody(UserDetailResponse::class.java)
            .value { response ->
                assertThat(response.id).isEqualTo(userId)
                assertThat(response.email).isEqualTo(email)
                assertThat(response.nickname).isEqualTo(nickname)
            }
        // then
        coVerify { userFindUseCase.findById(userId) }
    }


    @Test
    fun `존재하지 않는 id로 조회 요청시 404 예외 반환`() {
        // given
        val userId = 999L
        coEvery { userFindUseCase.findById(userId) } throws UserNotFoundException()

        webTestClient.get()
            .uri("/user-service/$userId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody(ErrorResponse::class.java)
            .value { response ->
                assertThat(response.message).isEqualTo("유저가 존재하지 않습니다.")
                assertThat(response.code).isEqualTo(404)
            }

        coVerify { userFindUseCase.findById(userId) }
    }

    @Test
    fun `내 정보 조회 요청 정상 동작`() {
        // given
        val userId = 1L
        val email = "test@example.com"
        val accessToken = "testAccessToken"
        val nickname = "testUser"
        val userDto = UserDto(
            id = userId,
            email = email,
            nickname = nickname,
            authType = AuthType.EMAIL_PASSWORD,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        coEvery { userFindUseCase.findById(userId) } returns userDto

        // when & then
        webTestClient.get().uri("/user-service/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .header("userId", userId.toString())
            .header("email", email)
            .exchange()
            .expectStatus().isOk
            .expectBody(UserDetailResponse::class.java)
            .value { response ->
                assertThat(response).isNotNull
                assertThat(response.id).isEqualTo(userId)
                assertThat(response.email).isEqualTo(email)
            }

        coVerify { userFindUseCase.findById(userId) }
    }

    @Test
    fun `헤더에 토큰이 없이 내 정보 조회시 예외가 발생`() {
        // when & then
        webTestClient.get().uri("/user-service/me")
            .exchange()
            .expectStatus().is4xxClientError
    }
}