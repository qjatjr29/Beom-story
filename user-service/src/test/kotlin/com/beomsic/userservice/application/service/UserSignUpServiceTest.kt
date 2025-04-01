package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.application.service.auth.UserSignUpService
import com.beomsic.userservice.domain.exception.InvalidEmailException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.UserEmailAlreadyException
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.domain.model.User
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserSignUpServiceTest {

    @MockK
    private lateinit var userSignUpPort: UserSignUpPort

    @MockK
    private lateinit var validationService: ValidationService

    @InjectMockKs
    private lateinit var userSignUpService: UserSignUpService

    @BeforeEach
    fun setUp() {
        clearMocks(userSignUpPort, validationService) // 테스트마다 Mock 초기화
    }

    companion object {
        @JvmStatic
        fun invalidEmails() = listOf(
            "plainaddress",
            "username.com",
            "@missingusername.com",
            "username@.com.my",
            "username@com",
            "username@domain..com"
        )

        @JvmStatic
        fun invalidPasswords() = listOf(
            "short1!",
            "alllowercase!",
            "ALLUPPERCASE1",
            "12345678!",
            "noSpecialChar123"
        )
    }

    @Test
    fun `사용자 회원가입 정상 동작 테스트`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password12@",
            nickname = "user"
        )

        val user = User(
            id = 1,
            email = command.email,
            nickname = command.nickname,
            password = command.password,
            authType = AuthType.EMAIL_PASSWORD,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // when
        coEvery { validationService.validateEmail(command.email) } just Runs
        coEvery { validationService.validatePassword(command.password) } just Runs
        coEvery { userSignUpPort.signup(command) } returns user

        userSignUpService.execute(command)

        // then
        coVerify { userSignUpPort.signup(command) }
    }

    @Test
    fun `사용자 회원가입시 이미 존재하는 이메일이면 회원가입 실패`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password12@",
            nickname = "user"
        )
        // when
        coEvery { validationService.validateEmail(command.email) } just Runs
        coEvery { validationService.validatePassword(command.password) } just Runs

        coEvery { userSignUpPort.signup(command) } throws UserEmailAlreadyException()

        // then
        assertThrows<UserEmailAlreadyException> { userSignUpService.execute(command) }

        coVerify(exactly = 1) { validationService.validateEmail(command.email) }
        coVerify(exactly = 1) { validationService.validatePassword(command.password) }
        coVerify(exactly = 1) { userSignUpPort.signup(command) }
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    fun `사용자 회원가입시 유효한 형식이 아닌 이메일이라면 회원가입 실패`(email: String) = runTest {
        // given
        val command = UserSignUpCommand(
            email = email,
            password = "password12!",
            nickname = "user"
        )

        // when
        coEvery { validationService.validateEmail(command.email) } throws InvalidEmailException()

        assertThrows<InvalidEmailException> {
            userSignUpService.execute(command)
        }

        // then
        coVerify(exactly = 1) { validationService.validateEmail(command.email) }
        coVerify(exactly = 0) { validationService.validatePassword(command.password) }
        coVerify(exactly = 0) { userSignUpPort.signup(command) }
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    fun `사용자 회원가입 시 유효한 형식이 아닌 패스워드라면 회원가입 실패`(password: String) = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = password,
            nickname = "user"
        )

        // when
        coEvery { validationService.validateEmail(command.email) } just Runs
        coEvery { validationService.validatePassword(command.password) } throws InvalidPasswordException()

        assertThrows<InvalidPasswordException> {
            userSignUpService.execute(command)
        }

        // then
        coVerify(exactly = 1) { validationService.validateEmail(command.email) }
        coVerify(exactly = 1) { validationService.validatePassword(command.password) }
        coVerify(exactly = 0) { userSignUpPort.signup(command) }
    }

}