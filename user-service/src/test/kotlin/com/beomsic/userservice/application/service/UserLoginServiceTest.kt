package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserLoginCommand
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.port.out.UserLoginPort
import com.beomsic.userservice.application.service.auth.UserLoginService
import com.beomsic.userservice.domain.exception.InvalidJwtTokenException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.PasswordNotMatchedException
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.domain.model.User
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserLoginServiceTest {

    @MockK
    private lateinit var userFindPort: UserFindPort

    @MockK
    private lateinit var userLoginPort: UserLoginPort

    @InjectMockKs
    private lateinit var loginService: UserLoginService

    @BeforeEach
    fun setUp() {
        clearMocks(userFindPort, userLoginPort)
    }

    @Nested
    inner class `로그인 테스트` {

        @BeforeEach
        fun setUp() {
            mockkObject(BCryptUtils)
        }

        @Test
        fun `로그인 정상 테스트`() = runTest {
            // given
            val userId = 1L
            val userEmail = "test@test.com"
            val exceptedToken = "testToken"
            val userLoginCommand = UserLoginCommand(email = userEmail, password = "test1234!")
            val user = User(
                id = userId,
                email = userEmail,
                password = userLoginCommand.password,
                nickname = "test",
                authType = AuthType.EMAIL_PASSWORD,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now())

            // when
            coEvery { userFindPort.findByEmail(userEmail) } returns user
            coEvery { BCryptUtils.verify(any(), any()) } returns true
            coEvery { userLoginPort.login(userId = userId, email = userEmail) } returns exceptedToken

            // then
            val result = loginService.login(userLoginCommand)
            coVerify(exactly = 1) { userFindPort.findByEmail(userEmail) }
            coVerify(exactly = 1) { userLoginPort.login(userId = userId, email = userEmail) }
            Assertions.assertThat(result.id).isEqualTo(userId)
            Assertions.assertThat(result.email).isEqualTo(userEmail)
            Assertions.assertThat(result.accessToken).isEqualTo(exceptedToken)
        }

        @Test
        fun `입력된 이메일로 회원가입한 유저가 없는 경우 로그인 실패`() = runTest {
            // given
            val userEmail = "test@test.com"
            val userLoginCommand = UserLoginCommand(email = userEmail, password = "test1234!")

            // when
            coEvery { userFindPort.findByEmail(userEmail) } throws UserNotFoundException()

            // then
            assertThrows<UserNotFoundException> {
                loginService.login(userLoginCommand)
            }
        }

        @Test
        fun `해당 유저의 패스워드가 없는 경우(소셜 로그인) 로그인 실패`() = runTest {
            // given
            val userId = 1L
            val userEmail = "test@test.com"
            val userLoginCommand = UserLoginCommand(email = userEmail, password = "test1234!")
            val user = User(
                id = userId,
                email = userEmail,
                nickname = "test",
                authType = AuthType.EMAIL_PASSWORD,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now())

            // when
            coEvery { userFindPort.findByEmail(userEmail) } returns user

            // then
            assertThrows<InvalidPasswordException> {
                loginService.login(userLoginCommand)
            }
        }

        @Test
        fun `로그인 입력 패스워드가 실제 패스워드와 다른 경우 로그인 실패`() = runTest {
            // given
            val userId = 1L
            val userEmail = "test@test.com"
            val userLoginCommand = UserLoginCommand(email = userEmail, password = "test1234!")
            val user = User(
                id = userId,
                email = userEmail,
                password = BCryptUtils.hash("test123!"),
                nickname = "test",
                authType = AuthType.EMAIL_PASSWORD,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now())

            // when
            coEvery { userFindPort.findByEmail(userEmail) } returns user

            // then
            assertThrows<PasswordNotMatchedException> {
                loginService.login(userLoginCommand)
            }
        }
    }

    @Nested
    inner class `로그아웃 테스트` {

        @Test
        fun `정상 로그아웃`() = runTest {
            // given
            val userId = 1L
            val accessToken = "testToken"
            val user = User(
                id = userId,
                email = "test@test.com",
                nickname = "test",
                authType = AuthType.EMAIL_PASSWORD,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now())

            // when
            coEvery { userFindPort.findById(userId) } returns user
            coEvery { userLoginPort.logout(userId, accessToken) } just Runs

            // then
            loginService.logout(userId, accessToken)

            coVerify(exactly = 1) { userFindPort.findById(userId) }
            coVerify(exactly = 1) { userLoginPort.logout(userId, accessToken) }
        }

        @Test
        fun `유저가 존재하지 않는 경우 로그아웃 실패`() = runTest {
            // given
            val userId = 1L
            val accessToken = "testToken"

            // when
            coEvery { userFindPort.findById(userId) } throws UserNotFoundException()

            // then
            assertThrows<UserNotFoundException> {
                loginService.logout(userId, accessToken)
            }

            coVerify(exactly = 1) { userFindPort.findById(userId) }
            coVerify(exactly = 0) { userLoginPort.logout(any(), any()) }
        }

        @Test
        fun `로그아웃 중 로그인 포트에서 예외 발생 시 실패`() = runTest {
            // given
            val userId = 1L
            val accessToken = "validAccessToken"
            val user = User(
                id = userId,
                email = "test@test.com",
                password = "hashedPassword",
                nickname = "test",
                authType = AuthType.EMAIL_PASSWORD,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            // when
            coEvery { userFindPort.findById(userId) } returns user
            coEvery { userLoginPort.logout(userId, accessToken) } throws RuntimeException("로그아웃 실패")

            // then
            assertThrows<RuntimeException> {
                loginService.logout(userId, accessToken)
            }

            coVerify(exactly = 1) { userFindPort.findById(userId) }
            coVerify(exactly = 1) { userLoginPort.logout(userId, accessToken) }
        }

    }

    @Nested
    inner class `토큰 갱신 테스트` {

        @Test
        fun `정상적인 리프레시 토큰 요청 시 새로운 액세스 토큰 반환`() = runTest {
            // given
            val refreshToken = "accessToken"
            val newAccessToken = "newAccessToken"

            // when
            coEvery { userLoginPort.reissue(refreshToken) } returns newAccessToken

            // then
            val result = loginService.reissueToken(refreshToken)
            Assertions.assertThat(result).isEqualTo(newAccessToken)

            coVerify(exactly = 1) { userLoginPort.reissue(refreshToken) }
        }

        @Test
        fun `리프레시 토큰이 유효하지 않은 경우 토큰 갱신 실패`() = runTest {
            // given
            val refreshToken = "invalidRefreshToken"

            // when
            coEvery { userLoginPort.reissue(refreshToken) } throws InvalidJwtTokenException()

            // then
            assertThrows<InvalidJwtTokenException> {
                loginService.reissueToken(refreshToken)
            }

            coVerify(exactly = 1) { userLoginPort.reissue(refreshToken) }
        }

        @Test
        fun `토큰 갱신 중 예외 발생 시 실패`() = runTest {
            // given
            val refreshToken = "validRefreshToken"

            // when
            coEvery { userLoginPort.reissue(refreshToken) } throws RuntimeException()

            // then
            assertThrows<RuntimeException> {
                loginService.reissueToken(refreshToken)
            }

            coVerify(exactly = 1) { userLoginPort.reissue(refreshToken) }
        }
    }

}