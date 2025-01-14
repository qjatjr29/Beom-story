package com.beomsic.userservice.application.service

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.domain.exception.InvalidEmailException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.UserExistsException
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired


@ExtendWith(MockKExtension::class)
class UserSignUpServiceTest {

    @MockK
    private lateinit var userFindPort: UserFindPort

    @MockK
    private lateinit var userSignUpPort: UserSignUpPort

    @MockK
    private lateinit var validationService: ValidationService

    @Autowired
    private lateinit var userSignUpService: UserSignUpService

    @BeforeEach
    fun setUp() {
        clearMocks(userFindPort, userSignUpPort) // 테스트마다 Mock 초기화
        userSignUpService = UserSignUpService(userFindPort, userSignUpPort, validationService)
    }

    @Test
    fun `사용자 회원가입 테스트`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password12@",
            username = "user"
        )

        // when
        coEvery { validationService.validateEmail(command.email) } just Runs
        coEvery { validationService.validatePassword(command.password) } just Runs
        coEvery { userFindPort.findByEmail(command.email) } returns null
        coEvery { userSignUpPort.signup(command) } just Runs

        userSignUpService.execute(command)

        // then
        coVerify { userFindPort.findByEmail(command.email) }
        coVerify { userSignUpPort.signup(command) }
    }

    @Test
    fun `사용자 회원가입시 이미 존재하는 이메일이면 UserExistsException을 던진다`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password12@",
            username = "user"
        )

        val existingUser = UserEntity(
            email = command.email,
            password = command.password,
            username = command.username
        )

        // when
        coEvery { validationService.validateEmail(command.email) } just Runs
        coEvery { validationService.validatePassword(command.password) } just Runs
        coEvery { userFindPort.findByEmail(command.email) } returns existingUser

        // then
        assertThrows<UserExistsException> { userSignUpService.execute(command) }
    }

    @Test
    fun `사용자 회원가입시 이메일이 유효하지 않으면 예외를 던진다`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "invalid-email",
            password = "password12!",
            username = "user"
        )

        // when
        coEvery { validationService.validateEmail(command.email) } throws InvalidEmailException()

        assertThrows<InvalidEmailException> {
            userSignUpService.execute(command)
        }

        // then
        coVerify { validationService.validateEmail(command.email) }
        coVerify(exactly = 0) { userFindPort.findByEmail(command.email) }
        coVerify(exactly = 0) { userSignUpPort.signup(command) }
    }

    @Test
    fun `사용자 회원가입 시 패스워드가 유효하지 않으면 예외를 던진다`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "invalidpass",
            username = "user"
        )

        // when
        coEvery { validationService.validateEmail(command.email) } just Runs
        coEvery { validationService.validatePassword(command.password) } throws InvalidPasswordException()

        assertThrows<InvalidPasswordException> {
            userSignUpService.execute(command)
        }

        // then
        coVerify { validationService.validatePassword(command.password) }
        coVerify(exactly = 0) { userFindPort.findByEmail(command.email) }
        coVerify(exactly = 0) { userSignUpPort.signup(command) }
    }

}