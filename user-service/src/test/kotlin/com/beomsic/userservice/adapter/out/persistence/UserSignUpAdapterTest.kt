package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.domain.util.BCryptUtils
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired

@ExtendWith(MockKExtension::class)
class UserSignUpAdapterTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userSignUpAdapter: UserSignUpAdapter

    @BeforeEach
    fun setUp() {
        clearMocks(userRepository)
        userSignUpAdapter = UserSignUpAdapter(userRepository)
    }

    @Test
    fun `회원가입`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password",
            nickname = "user"
        )

        val userEntity = UserEntity(
            email = command.email,
            password = BCryptUtils.hash(command.password),
            nickname = command.nickname
        )

        coEvery { userRepository.save(any()) } returns userEntity

        // when
        userSignUpAdapter.signup(command)

        // then
        coVerify { userRepository.save(match { it.email == command.email }) }
    }

}