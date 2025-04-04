package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.adapter.out.persistence.adapter.UserUpdateAdapter
import com.beomsic.userservice.domain.exception.PasswordNotMatchedException
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.stream.Stream

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserUpdateAdapterTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var userUpdateAdapter: UserUpdateAdapter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepository)
    }

    companion object {
        @JvmStatic
        fun authUserData(): Stream<Arguments> = Stream.of(
            Arguments.of(AuthType.EMAIL_PASSWORD, "password12!", null),
            Arguments.of(AuthType.HYBRID, "password12!", "GOOGLE"),
            Arguments.of(AuthType.OAUTH, null, "GOOGLE"),
        )
    }

    @Nested
    inner class `닉네임 업데이트 테스트` {
        @Test
        fun `닉네임 변경 성공`() = runTest {
            // given
            val userId = 1L
            val newNickname = "newNickname"
            val userEntity = UserEntity(
                id = userId,
                email = "user@test.com",
                password = "password12!",
                nickname = "oldNickname",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

            coEvery { userRepository.findByIdOrNull(userId) } returns userEntity
            coEvery { userRepository.save(any()) } answers { firstArg() }

            // when
            userUpdateAdapter.updateNickname(userId, newNickname)

            // then
            coVerify {
                userRepository.save(withArg { updatedUser ->
                    assertThat(updatedUser.nickname).isEqualTo(newNickname)
                })
            }
        }

        @Test
        fun `존재하지 않는 유저 id로 닉네임 변경 시 예외 발생`() = runTest {
            // given
            val userId = 999L
            coEvery { userRepository.findByIdOrNull(userId) } throws UserNotFoundException()

            // when & then
            assertThrows<UserNotFoundException> {
                userUpdateAdapter.updateNickname(userId, "newNickname")
            }
        }
    }

    @Nested
    inner class `비밀번호 업데이트 테스트` {

        @ParameterizedTest(name = "{0}")
        @MethodSource("com.beomsic.userservice.adapter.out.persistence.UserUpdateAdapterTest#authUserData")
        fun `유저 Auth Type별 비밀번호 변경 성공`(authType: AuthType, password: String?, provider: String?) = runTest {
            // given
            val userId = 1L
            val newPassword = "newPassword456"
            val hashedOldPassword = password?.let { BCryptUtils.hash(it) }
            val userEntity = UserEntity(
                id = userId,
                email = "user@test.com",
                nickname = "testUser",
                provider = provider,
                password = hashedOldPassword,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            coEvery { userRepository.findByIdOrNull(userId) } returns userEntity
            coEvery { userRepository.save(any()) } answers { firstArg() }

            // when
            userUpdateAdapter.updatePassword(userId, password ?: "", newPassword)

            // then
            coVerify {
                userRepository.save(withArg { updatedUser ->
                    assertThat(BCryptUtils.verify(newPassword, updatedUser.password!!)).isTrue()
                })
            }
        }

        @Test
        fun `현재 비밀번호가 틀릴 경우 예외 발생`() = runTest {
            // given
            val userId = 1L
            val wrongPassword = "wrongPassword"
            val correctPassword = "correctPassword"
            val userEntity = UserEntity(
                id = userId,
                email = "user@test.com",
                nickname = "testUser",
                password = BCryptUtils.hash(correctPassword),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            coEvery { userRepository.findByIdOrNull(userId) } returns userEntity

            // when & then
            assertThrows<PasswordNotMatchedException> {
                userUpdateAdapter.updatePassword(userId, wrongPassword, "newPassword")
            }
        }
    }

    @Nested
    inner class `유저 삭제 테스트` {

        @Test
        fun `유저 삭제 성공`() = runTest {
            // given
            val userId = 1L
            coEvery { userRepository.deleteById(userId) } just Runs

            // when
            userUpdateAdapter.deleteUser(userId)

            // then
            coVerify { userRepository.deleteById(userId) }
        }
    }

}