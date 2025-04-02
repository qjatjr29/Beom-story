package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.adapter.out.persistence.adapter.UserFindAdapter
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.domain.model.User
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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserFindAdapterTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var userFindAdapter: UserFindAdapter

    @BeforeEach
    fun setup() {
        clearMocks(userRepository)
    }

    @Test
    fun `Id를 통한 유저 조회 정상 동작`() = runTest {
        // given
        val userId = 1L
        val email = "user@example.com"
        val nickname = "testUser"
        val userEntity = UserEntity(
            id = userId,
            email = email,
            password = "password12!",
            nickname = nickname,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        coEvery { userRepository.findByIdOrNull(userId) } returns userEntity

        // when
        val result = userFindAdapter.findById(userId)

        // then
        assertThat(result.id).isEqualTo(userId)
        assertThat(result.email).isEqualTo(email)
        assertThat(result.nickname).isEqualTo(nickname)
        coVerify(exactly = 1) { userRepository.findByIdOrNull(userId) }
    }

    @Test
    fun `존재하지 않는 ID로 유저 조회 시 예외 발생`() = runTest {
        // given
        val userId = 999L
        coEvery { userRepository.findById(userId) } returns null

        // when & then
        assertThrows<UserNotFoundException> {
            userFindAdapter.findById(userId)
        }
        coVerify(exactly = 1) { userRepository.findByIdOrNull(userId) }
    }

    @Test
    fun `이메일을 통한 유저 조회 정상 동작`() = runTest {
        // given
        val userId = 1L
        val email = "invalid@example.com"
        val nickname = "testUser"
        val userEntity = UserEntity(
            id = userId,
            email = email,
            password = "password12!",
            nickname = nickname,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        coEvery { userRepository.findByEmailOrNull(email) } returns userEntity

        // when
        val result = userFindAdapter.findByEmail(email)

        // then
        assertThat(result.id).isEqualTo(userId)
        assertThat(result.email).isEqualTo(email)
        assertThat(result.nickname).isEqualTo(nickname)
        coVerify(exactly = 1) { userRepository.findByEmailOrNull(email) }
    }

    @Test
    fun `존재하지 않는 이메일로 유저 조회 시 예외 발생`() = runTest {
        // given
        val invalidEmail = "non@example.com"
        coEvery { userRepository.findByEmail(invalidEmail) } returns null

        // when & then
        assertThrows<UserNotFoundException> {
            userFindAdapter.findByEmail(invalidEmail)
        }
        coVerify(exactly = 1) { userRepository.findByEmailOrNull(invalidEmail) }
    }

    @Test
    fun `소셜 provider 유저 조회 정상 동작`() = runTest {
        // given
        val userId = 1L
        val email = "invalid@example.com"
        val nickname = "testUser"
        val provider = "GOOGLE"
        val providerId = "123"
        val userEntity = UserEntity(
            id = userId,
            email = email,
            provider = provider,
            providerId = providerId,
            nickname = nickname,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
        coEvery { userRepository.findByProviderAndProviderId(provider, providerId) } returns userEntity

        // when
        val result = userFindAdapter.findByProviderAndProviderId(provider, providerId)

        // then
        assertThat(result)
            .isNotNull()
            .extracting("id", "email", "nickname")
            .containsExactly(userId, email, nickname)
        assertThat(result).isExactlyInstanceOf(User::class.java)
        coVerify(exactly = 1) { userRepository.findByProviderAndProviderId(provider, providerId) }
    }

    @Test
    fun `존재하지 않는 소셜 provider으로 유저 조회 시 Null 반환`() = runTest {
        // given
        val provider = "GOOGLE"
        val providerId = "123"
        coEvery { userRepository.findByProviderAndProviderId(provider, providerId) } returns null

        // when
        val result = userFindAdapter.findByProviderAndProviderId(provider, providerId)

        // then
        assertThat(result).isNull()
        coVerify(exactly = 1) { userRepository.findByProviderAndProviderId(provider, providerId) }
    }


}