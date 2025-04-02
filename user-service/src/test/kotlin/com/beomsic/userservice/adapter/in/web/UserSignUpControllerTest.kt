package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.adapter.`in`.web.dto.SignUpRequest
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserSignUpUseCase
import com.beomsic.userservice.domain.exception.InvalidEmailException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.UserEmailAlreadyException
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test

@AutoConfigureWebTestClient
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserSignUpControllerTest {

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
    fun `일반(이메일, 패스워드) 회원가입 요청 정상 동작`() {
        // given
        val request = SignUpRequest(
            email = "user@example.com",
            password = "password12!",
            nickname = "user"
        )
        val command = UserSignUpCommand(
            email = request.email,
            password = request.password,
            nickname = request.nickname
        )

        coEvery { userSignUpUseCase.execute(command) } just Runs

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated

        // then
        coVerify { userSignUpUseCase.execute(
            match { it.email == request.email && it.nickname == request.nickname }
        )}
    }

    @Test
    fun `잘못된 이메일 형식으로 회원가입 요청시 회원가입 실패`() {
        // given
        val request = SignUpRequest(
            email = "user@examplecom",
            password = "password12!",
            nickname = "user"
        )
        val command = UserSignUpCommand(
            email = request.email,
            password = request.password,
            nickname = request.nickname
        )

        coEvery { userSignUpUseCase.execute(command) } throws InvalidEmailException()

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)

        // then
        coVerify { userSignUpUseCase.execute(command) }
    }

    @Test
    fun `잘못된 비밀번호 형식으로 회원가입 요청시 회원가입 실패`() {
        // given
        val request = SignUpRequest(
            email = "user@example.com",
            password = "password12",
            nickname = "user"
        )
        val command = UserSignUpCommand(
            email = request.email,
            password = request.password,
            nickname = request.nickname
        )

        coEvery { userSignUpUseCase.execute(command) } throws InvalidPasswordException()

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)

        // then
        coVerify { userSignUpUseCase.execute(command) }
    }

    @Test
    fun `이미 회원가입된 이메일로 회원가입 요청시 회원가입 실패`() {
        // given
        val request = SignUpRequest(
            email = "user@example.com",
            password = "password12",
            nickname = "user"
        )
        val command = UserSignUpCommand(
            email = request.email,
            password = request.password,
            nickname = request.nickname
        )

        coEvery { userSignUpUseCase.execute(command) } throws UserEmailAlreadyException()

        // when
        webTestClient.post()
            .uri("/user-service/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)

        // then
        coVerify { userSignUpUseCase.execute(command) }
    }
}