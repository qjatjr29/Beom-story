package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.domain.exception.UserNotFoundException
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

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
    fun `유저 조회 - Id를 통해 유저 조회`() = runTest {

        // given
        val userId = 1L
        val userEntity = UserEntity(
            id = userId,
            email = "test@example.com",
            password = "password12!",
            nickname = "testUser"
        )
        coEvery { userRepository.findByIdOrNull(userId) } returns userEntity

        // when
        val result = userFindAdapter.findById(userId)

        // then
        assertEquals(userId, result.id)
        assertEquals("test@example.com", result.email)
        assertEquals("testUser", result.nickname)
        coVerify { userRepository.findByIdOrNull(userId) }
    }

    @Test
    fun `유저 조회 - 존재하지 않는 ID로 조회 시 예외 발생`() = runTest {
        // given
        val userId = 999L
        coEvery { userRepository.findByIdOrNull(userId) } throws UserNotFoundException()

        // when & then
        assertThrows<UserNotFoundException> {
            userFindAdapter.findById(userId)
        }
        coVerify { userRepository.findByIdOrNull(userId) }
    }


}