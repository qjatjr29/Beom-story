package com.beomsic.userservice.application.service

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.application.port.out.UserFindPort
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
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserFindServiceTest {

    @InjectMockKs
    private lateinit var userFindService: UserFindService

    @MockK
    private lateinit var userFindPort: UserFindPort

    @BeforeEach
    fun setup() {
        clearMocks(userFindPort)
    }

    @Test
    fun `유저 조회 - Id로 조회`() = runTest {

        // given
        val userId = 1L
        val userEntity = UserEntity(
            id = userId,
            email = "test@example.com",
            password = "password12!",
            nickname = "testUser",
            createdAt = LocalDateTime.of(2025, 1, 1, 12, 30),
            updatedAt = LocalDateTime.of(2025, 1, 1, 12, 30)
        )
        coEvery { userFindPort.findById(userId) } returns userEntity

        // when
        val result = userFindService.findById(userId)

        // then
        assertEquals(userId, result.id)
        assertEquals("test@example.com", result.email)
        assertEquals("testUser", result.nickname)
        coVerify { userFindPort.findById(userId) }
    }

    @Test
    fun `유저 조회 - 없는 Id로 조회시 exception throw`() = runTest {

        // given
        val userId = 1L
        coEvery { userFindPort.findById(any()) } throws UserNotFoundException()

        // when
        // then
        assertThrows<UserNotFoundException> {
            userFindService.findById(userId) // suspend 함수 호출
        }
        coVerify { userFindPort.findById(userId) }
    }
}
