package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.out.CheckDuplicatePort
import com.beomsic.userservice.domain.exception.InvalidException
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class CheckDuplicateServiceTest {

    @InjectMockKs
    private lateinit var checkDuplicateService: CheckDuplicateService

    @MockK
    private lateinit var validationService: ValidationService

    @MockK
    private lateinit var checkDuplicatePort: CheckDuplicatePort

    @AfterEach
    fun clear() {
        clearMocks(validationService, checkDuplicatePort)
    }

    @Test
    fun `이메일 중복 확인 - 이미 존재하는 경우`() = runTest {

        // given
        val givenEmail = "test@example.com"
        coEvery { validationService.validateEmail(givenEmail) } just runs
        coEvery { checkDuplicatePort.isDuplicatedEmail(givenEmail) } returns true

        // when
        val result = checkDuplicateService.execute("email", givenEmail)

        // then
        assertEquals(true, result)
        coVerify { checkDuplicatePort.isDuplicatedEmail(givenEmail) }
    }

    @Test
    fun `이메일 중복 확인 - 존재하지 않는 경우`() = runTest {

        // given
        val givenEmail = "test@example.com"
        coEvery { validationService.validateEmail(givenEmail) } just runs
        coEvery { checkDuplicatePort.isDuplicatedEmail(givenEmail) } returns false

        // when
        val result = checkDuplicateService.execute("email", givenEmail)

        // then
        assertEquals(false, result)
        coVerify { checkDuplicatePort.isDuplicatedEmail(givenEmail) }
    }

    @Test
    fun `닉네임 중복 확인 - 존재하는 경우`() = runTest {

        // given
        val givenNickname = "testUser"
        coEvery { checkDuplicatePort.isDuplicatedNickname(givenNickname) } returns true

        // when
        val result = checkDuplicateService.execute("nickname", givenNickname)

        // then
        assertEquals(true, result)
        coVerify { checkDuplicatePort.isDuplicatedNickname(givenNickname) }
    }

    @Test
    fun `닉네임 중복 확인 - 존재하지 않는 경우`() = runTest {

        // given
        val givenNickname = "testUser"
        coEvery { checkDuplicatePort.isDuplicatedNickname(givenNickname) } returns false

        // when
        val result = checkDuplicateService.execute("nickname", givenNickname)

        // then
        assertEquals(false, result)
        coVerify { checkDuplicatePort.isDuplicatedNickname(givenNickname) }
    }

    @Test
    fun `타입이 잘못된 경우 - InvalidException 발생`() = runTest {

        // given
        val invalidType = "invalid"
        val givenValue = "test@example.com"

        // when + then

        val exception = assertThrows<InvalidException> {
            checkDuplicateService.execute(invalidType, givenValue)
        }
        assertEquals("중복 검사를 하는 타입이 잘못되었습니다: $invalidType", exception.message)
    }

}