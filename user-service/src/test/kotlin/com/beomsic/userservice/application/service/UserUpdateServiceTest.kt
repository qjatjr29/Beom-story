package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserNicknameUpdateCommand
import com.beomsic.userservice.application.port.`in`.command.UserPasswordUpdateCommand
import com.beomsic.userservice.application.port.out.UserUpdatePort
import com.beomsic.userservice.domain.exception.AuthenticationException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import com.beomsic.userservice.domain.exception.PasswordNotMatchedException
import com.beomsic.userservice.domain.exception.UserNotFoundException
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserUpdateServiceTest {

    @MockK
    lateinit var userUpdatePort: UserUpdatePort

    @MockK
    lateinit var validationService: ValidationService

    @InjectMockKs
    lateinit var userUpdateService: UserUpdateService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class `닉네임 업데이트 테스트` {
        @Test
        fun `닉네임 업데이트 정상 동작`() = runTest {
            // given
            val userId = 1L
            val authUserId = 1L
            val newNickname = "newNickname"
            val command = UserNicknameUpdateCommand(userId = userId, authUserId = authUserId, nickName = newNickname)
            val nicknameSlot = slot<String>()
            val userIdSlot = slot<Long>()
            coEvery { userUpdatePort.updateNickname(capture(userIdSlot), capture(nicknameSlot)) } just Runs

            // when
            userUpdateService.updateUserNickname(command)

            // then
            coVerify { userUpdatePort.updateNickname(userId, newNickname) }
            assertThat(userIdSlot.captured).isEqualTo(userId)
            assertThat(nicknameSlot.captured).isEqualTo(newNickname)
        }

        @Test
        fun `요청한 유저와 업데이트할 유저가 다른 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 2L
            val nickName = "newNickname"
            val command = UserNicknameUpdateCommand(userId = userId, authUserId = authUserId, nickName = nickName)

            // when
            assertThrows<AuthenticationException> {
                userUpdateService.updateUserNickname(command)
            }
        }

        @Test
        fun `userId에 해당하는 유저가 없는 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 1L
            val nickName = "newNickname"
            val command = UserNicknameUpdateCommand(userId = userId, authUserId = authUserId, nickName = nickName)

            coEvery { userUpdatePort.updateNickname(userId, nickName) } throws UserNotFoundException()

            // when
            assertThrows<UserNotFoundException> {
                userUpdateService.updateUserNickname(command)
            }
        }
    }

    @Nested
    inner class `패스워드 업데이트 테스트` {
        @Test
        fun `패스워드 업데이트 정상 동작`() = runTest {
            // given
            val userId = 1L
            val authUserId = 1L
            val oldPassword = "oldPassword12!"
            val newPassword = "newPassword12!"
            val command = UserPasswordUpdateCommand(userId = userId,
                authUserId = authUserId,
                currentPassword = oldPassword,
                newPassword = newPassword
            )
            val oldPasswordSlot = slot<String>()
            val newPasswordSlot = slot<String>()
            val userIdSlot = slot<Long>()

            coEvery { validationService.validatePassword(capture(newPasswordSlot)) } just Runs
            coEvery { userUpdatePort.updatePassword(capture(userIdSlot),
                capture(oldPasswordSlot), capture(newPasswordSlot)) } just Runs

            // when
            userUpdateService.updateUserPassword(command)

            // then
            coVerify { userUpdatePort.updatePassword(userId, oldPassword, newPassword) }
            assertThat(userIdSlot.captured).isEqualTo(userId)
            assertThat(oldPasswordSlot.captured).isEqualTo(oldPassword)
            assertThat(newPasswordSlot.captured).isEqualTo(newPassword)
        }

        @Test
        fun `요청한 유저와 업데이트할 유저가 다른 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 2L
            val oldPassword = "oldPassword12!"
            val newPassword = "newPassword12!"
            val command = UserPasswordUpdateCommand(userId = userId,
                authUserId = authUserId,
                currentPassword = oldPassword,
                newPassword = newPassword
            )
            // when
            assertThrows<AuthenticationException> {
                userUpdateService.updateUserPassword(command)
            }
        }

        @Test
        fun `userId에 해당하는 유저가 없는 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 1L
            val oldPassword = "oldPassword12!"
            val newPassword = "newPassword12!"
            val command = UserPasswordUpdateCommand(userId = userId,
                authUserId = authUserId,
                currentPassword = oldPassword,
                newPassword = newPassword
            )

            coEvery { validationService.validatePassword(newPassword) } just Runs
            coEvery { userUpdatePort.updatePassword(userId, oldPassword, newPassword) } throws UserNotFoundException()

            // when
            assertThrows<UserNotFoundException> {
                userUpdateService.updateUserPassword(command)
            }
        }

        @Test
        fun `기존 패스워드가 틀렸을 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 1L
            val oldPassword = "oldPassword12!"
            val newPassword = "newPassword12!"
            val command = UserPasswordUpdateCommand(userId = userId,
                authUserId = authUserId,
                currentPassword = oldPassword,
                newPassword = newPassword
            )

            coEvery { validationService.validatePassword(newPassword) } just Runs
            coEvery { userUpdatePort.updatePassword(userId, oldPassword, newPassword) } throws PasswordNotMatchedException()

            // when
            assertThrows<PasswordNotMatchedException> {
                userUpdateService.updateUserPassword(command)
            }
        }

        @Test
        fun `새로운 패스워드 형식이 틀렸을 경우 예외 반환`() = runTest {
            // given
            val userId = 1L
            val authUserId = 1L
            val oldPassword = "oldPassword12!"
            val newPassword = "newPassword12"
            val command = UserPasswordUpdateCommand(userId = userId,
                authUserId = authUserId,
                currentPassword = oldPassword,
                newPassword = newPassword
            )

            coEvery { validationService.validatePassword(newPassword) } just Runs
            coEvery { userUpdatePort.updatePassword(userId, oldPassword, newPassword) } throws InvalidPasswordException()

            // when
            assertThrows<InvalidPasswordException> {
                userUpdateService.updateUserPassword(command)
            }
        }
    }


}