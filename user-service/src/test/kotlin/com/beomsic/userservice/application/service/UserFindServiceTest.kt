package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.domain.exception.UserNotFoundException
import com.beomsic.userservice.domain.model.AuthType
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
        val email = "test@example.com"
        val password = "password12!"
        val nickname = "testUser"
        val user = User(
            id = userId,
            email = email,
            password = password,
            nickname = nickname,
            authType = AuthType.EMAIL_PASSWORD,
            createdAt = LocalDateTime.of(2025, 1, 1, 12, 30),
            updatedAt = LocalDateTime.of(2025, 1, 1, 12, 30)
        )
        coEvery { userFindPort.findById(userId) } returns user

        // when
        val result = userFindService.findById(userId)

        // then
        assertThat(result.id).isEqualTo(userId)
        assertThat(result.email).isEqualTo(email)
        assertThat(result.nickname).isEqualTo(nickname)
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
