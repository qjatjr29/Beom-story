package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.adapter.out.persistence.adapter.CheckDuplicateAdapter
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class CheckDuplicateAdapterTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var checkDuplicateAdapter: CheckDuplicateAdapter

    @AfterEach
    fun clear() {
        clearMocks(userRepository)
    }

    @Test
    fun `이메일 중복 체크 - 이미 존재하는 경우`() = runTest {
        // given
        val email = "test@example.com"

        coEvery { userRepository.existsByEmail(email) } returns true

        // when
        val result = checkDuplicateAdapter.isDuplicatedEmail(email)

        // then
        assertThat(result).isTrue()
        coVerify { userRepository.existsByEmail(email) }
    }

    @Test
    fun `이메일 중복 체크 - 존재하지 않는 경우`() = runTest {
        // given
        val email = "test@example.com"

        coEvery { userRepository.existsByEmail(email) } returns false

        // when
        val result = checkDuplicateAdapter.isDuplicatedEmail(email)

        // then
        assertThat(result).isFalse()
        coVerify { userRepository.existsByEmail(email) }
    }


    @Test
    fun `닉네임 중복 체크 - 이미 존재하는 경우`() = runTest {
        // given
        val nickname = "testUser"

        coEvery { userRepository.existsByNickname(nickname) } returns true

        // when
        val result = checkDuplicateAdapter.isDuplicatedNickname(nickname)

        // then
        assertThat(result).isTrue()
        coVerify { userRepository.existsByNickname(nickname) }
    }


    @Test
    fun `닉네임 중복 체크 - 존재하지 않는 경우`() = runTest {
        // given
        val nickname = "testUser"

        coEvery { userRepository.existsByNickname(nickname) } returns false

        // when
        val result = checkDuplicateAdapter.isDuplicatedNickname(nickname)

        // then
        assertThat(result).isFalse()
        coVerify { userRepository.existsByNickname(nickname) }
    }


}