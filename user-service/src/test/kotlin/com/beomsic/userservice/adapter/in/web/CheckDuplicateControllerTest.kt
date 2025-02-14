package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.application.port.`in`.usecase.CheckDuplicateUseCase
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class CheckDuplicateControllerTest {

    private lateinit var webTestClient: WebTestClient

    @MockK
    private lateinit var checkDuplicateUseCase: CheckDuplicateUseCase

    @InjectMockKs
    private lateinit var checkDuplicateController: CheckDuplicateController

    @BeforeEach
    fun setUp() {
        val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()
        webTestClient = WebTestClient
            .bindToController(checkDuplicateController)
            .controllerAdvice(globalExceptionHandler)
            .configureClient()
            .build()

        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @AfterEach
    fun close() {
        clearMocks(checkDuplicateUseCase)
    }

    @Test
    fun `이메일 중복 확인 - 존재하는 이메일`() = runTest {
        // given
        val type = "email"
        val value = "test@example.com"
        coEvery { checkDuplicateUseCase.execute(type, value) } returns true

        // when
        webTestClient.post()
            .uri("/user-service/duplicate?type=$type&value=$value")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.isDuplicated").isEqualTo(true)

        // then
        coVerify { checkDuplicateUseCase.execute(type, value) }
    }

    @Test
    fun `이메일 중복 확인 - 존재하지 않는 이메일`() = runTest {
        // given
        val type = "email"
        val value = "test@example.com"
        coEvery { checkDuplicateUseCase.execute(type, value) } returns false

        // when
        webTestClient.post()
            .uri("/user-service/duplicate?type=$type&value=$value")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.isDuplicated").isEqualTo(false)

        // then
        coVerify { checkDuplicateUseCase.execute(type, value) }
    }

    @Test
    fun `닉네임 중복 확인 - 존재하는 닉네임`() = runTest {
        // given
        val type = "nickname"
        val value = "testUser"
        coEvery { checkDuplicateUseCase.execute(type, value) } returns true

        // when
        webTestClient.post()
            .uri("/user-service/duplicate?type=$type&value=$value")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.isDuplicated").isEqualTo(true)

        // then
        coVerify { checkDuplicateUseCase.execute(type, value) }
    }

    @Test
    fun `닉네임 중복 확인 - 존재하지 않는 닉네임`() = runTest {
        // given
        val type = "nickname"
        val value = "testUser"
        coEvery { checkDuplicateUseCase.execute(type, value) } returns false

        // when
        webTestClient.post()
            .uri("/user-service/duplicate?type=$type&value=$value")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.isDuplicated").isEqualTo(false)

        // then
        coVerify { checkDuplicateUseCase.execute(type, value) }
    }

}