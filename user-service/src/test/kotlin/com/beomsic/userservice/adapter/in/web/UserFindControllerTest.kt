package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.application.port.`in`.usecase.UserFindUseCase
import com.beomsic.userservice.domain.User
import com.beomsic.userservice.domain.exception.UserNotFoundException
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserFindControllerTest {

    @Autowired
    @InjectMockKs
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
            .controllerAdvice(globalExceptionHandler)
            .configureClient()
            .build()
    }

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        clearMocks(userFindUseCase)
    }

    @Test
    fun `유저 조회 - ID로 유저 조회`() = runTest {
        // given
        val userId = 1L
        val user = User(
            id = userId,
            email = "test@example.com",
            username = "testUser"
        )
        coEvery { userFindUseCase.findById(userId) } returns user

        // when
        webTestClient.get()
            .uri("/user-service/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(userId)
            .jsonPath("$.email").isEqualTo("test@example.com")
            .jsonPath("$.username").isEqualTo("testUser")

        // then
        coVerify { userFindUseCase.findById(userId) }
    }


    @Test
    fun `유저 조회 - 존재하지 않는 ID로 조회 시 404 반환`() {
        // given
        val userId = 999L
        coEvery { userFindUseCase.findById(userId) } throws UserNotFoundException()

        webTestClient.get()
            .uri("/user-service/$userId")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("유저가 존재하지 않습니다.")

        coVerify { userFindUseCase.findById(userId) }
    }


}