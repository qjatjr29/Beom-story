package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import com.beomsic.userservice.domain.exception.InvalidEmailException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserSignUpControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockK
    private lateinit var userSignUpUseCase: UserSignUpUseCase

    @BeforeEach
    fun setUp() {

        val globalExceptionHandler: GlobalExceptionHandler = GlobalExceptionHandler()
        webTestClient = WebTestClient
            .bindToController(UserSignUpController(userSignUpUseCase))
            .controllerAdvice(globalExceptionHandler)
            .configureClient()
            .build()
    }

    @AfterEach
    fun close() {
        clearMocks(userSignUpUseCase)
    }

    @Test
    fun `회원가입 POST 요청 api`() {
        // given
        val request = SignUpRequest(
            email = "user@example.com",
            password = "password12!",
            username = "user"
        )

        coEvery { userSignUpUseCase.execute(any()) } just Runs

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk

        // then
        coVerify { userSignUpUseCase.execute(
            match { it.email == "user@example.com" && it.username == "user" }
        )}
    }

    @Test
    fun `회원가입 요청 api - 잘못된 이메일 형식`() {
        // given
        val request = SignUpRequest(
            email = "user@examplecom",
            password = "password12!",
            username = "user"
        )

        coEvery { userSignUpUseCase.execute(any()) } throws InvalidEmailException()

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)

        // then
        coVerify { userSignUpUseCase.execute(any()) }
    }

    @Test
    fun `회원가입 요청 api - 잘못된 비밀번호 형식`() {
        // given
        val request = SignUpRequest(
            email = "user@example.com",
            password = "password12",
            username = "user"
        )

        coEvery { userSignUpUseCase.execute(any()) } throws InvalidPasswordException()

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)

        // then
        coVerify { userSignUpUseCase.execute(any()) }
    }
}