package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.adapter.out.persistence.adapter.UserSignUpAdapter
import com.beomsic.userservice.application.port.`in`.command.UserSignUpCommand
import com.beomsic.userservice.domain.exception.UserEmailAlreadyException
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.domain.oauth.GoogleUserInfo
import com.beomsic.userservice.domain.oauth.KakaoUserInfo
import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.domain.oauth.SocialType
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
class UserSignUpAdapterTest {

    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var userSignUpAdapter: UserSignUpAdapter

    companion object {
        @JvmStatic
        fun provideOAuthData(): Stream<Arguments> = Stream.of(
            Arguments.of(SocialType.GOOGLE, GoogleUserInfo("12345", "test@example.com", "Test User")),
            Arguments.of(
                SocialType.KAKAO,
                KakaoUserInfo("12345",
                    KakaoUserInfo.KakaoAccount("test@example.com",
                        KakaoUserInfo.KakaoProfile("Test User", null)))
            )
        )
    }

    @BeforeEach
    fun setUp() {
        clearMocks(userRepository)
        userSignUpAdapter = UserSignUpAdapter(userRepository)
    }

    @Test
    fun `회원가입 정상 동작`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password",
            nickname = "user"
        )

        val userEntity = UserEntity(
            id = 1L,
            email = command.email,
            password = BCryptUtils.hash(command.password),
            nickname = command.nickname,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        coEvery { userRepository.existsByEmail(command.email) } returns false
        coEvery { userRepository.save(any()) } returns userEntity

        // when
        val user = userSignUpAdapter.signup(command)

        // then
        coVerify(exactly = 1) { userRepository.save(match { it.email == command.email }) }
        assertThat(user.id).isEqualTo(userEntity.id)
        assertThat(user.email).isEqualTo(command.email)
        assertThat(user.authType).isEqualTo(AuthType.EMAIL_PASSWORD)
    }

    @Test
    fun `이미 회원가입된 이메일인 경우 회원가입 실패`() = runTest {
        // given
        val command = UserSignUpCommand(
            email = "user@example.com",
            password = "password",
            nickname = "user"
        )
        coEvery { userRepository.existsByEmail(command.email) } returns true

        // when
        // then
        assertThrows<UserEmailAlreadyException> {
            userSignUpAdapter.signup(command)
        }
        coVerify(exactly = 1) { userRepository.existsByEmail(command.email) }
        coVerify(exactly = 0) { userRepository.save(match { it.email == command.email }) }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideOAuthData")
    fun `가입하지 않은 소셜 로그인 회원가입 정상 동작`(socialType: SocialType, oAuthUserInfo: OAuthUserInfo) = runTest {
        // given
        val userEntity = UserEntity(
            id = 1L,
            email = oAuthUserInfo.email,
            provider = socialType.name,
            providerId = oAuthUserInfo.providerId,
            nickname = oAuthUserInfo.name,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        coEvery { userRepository.existsByEmail(oAuthUserInfo.email) } returns false
        coEvery { userRepository.save(any()) } returns userEntity

        // when
        val user = userSignUpAdapter.oauthSignup(oAuthUserInfo)

        // then
        coVerify(exactly = 1) { userRepository.save(match { it.email == oAuthUserInfo.email }) }
        assertThat(user.id).isEqualTo(userEntity.id)
        assertThat(user.email).isEqualTo(oAuthUserInfo.email)
        assertThat(user.authType).isEqualTo(AuthType.OAUTH)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideOAuthData")
    fun `이미 같은 이메일로 회원가입되어 있는 경우 소셜 회원가입 실패`(socialType: SocialType, oAuthUserInfo: OAuthUserInfo) = runTest {
        // given
        coEvery { userRepository.existsByEmail(oAuthUserInfo.email) } returns true

        // when
        // then
        assertThrows<UserEmailAlreadyException>() {
            userSignUpAdapter.oauthSignup(oAuthUserInfo)
        }
        coVerify(exactly = 1) { userRepository.existsByEmail(oAuthUserInfo.email) }
        coVerify(exactly = 0) { userRepository.save(match { it.email == oAuthUserInfo.email }) }
    }

}