package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.out.OAuthPort
import com.beomsic.userservice.application.port.out.UserAuthPort
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.application.service.auth.OAuthService
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.domain.model.User
import com.beomsic.userservice.domain.oauth.GoogleUserInfo
import com.beomsic.userservice.domain.oauth.KakaoUserInfo
import com.beomsic.userservice.domain.oauth.OAuthUserInfo
import com.beomsic.userservice.domain.oauth.SocialType
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class OAuthServiceTest {

    @MockK
    private lateinit var oauthPort: OAuthPort

    @MockK
    private lateinit var userFindPort: UserFindPort

    @MockK
    private lateinit var userAuthPort: UserAuthPort

    @MockK
    private lateinit var userSignUpPort: UserSignUpPort

    @InjectMockKs
    private lateinit var oauthService: OAuthService

    companion object {
        @JvmStatic
        fun provideOAuthData(): Stream<Arguments> = Stream.of(
            Arguments.of(SocialType.GOOGLE, GoogleUserInfo("12345", "test@example.com", "Test User")),
            Arguments.of(SocialType.KAKAO,
                KakaoUserInfo("12345",
                KakaoUserInfo.KakaoAccount("test@example.com",
                    KakaoUserInfo.KakaoProfile("Test User", null))))
        )
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(oauthPort, userFindPort, userAuthPort, userSignUpPort)
    }

    @Test
    fun `인증 코드 URI 반환`() = runTest {
        // given
        val provider = "google"
        val expectedUri = "https://accounts.google.com/o/oauth2/auth"

        coEvery { oauthPort.getAuthCodeUri(SocialType.GOOGLE) } returns expectedUri

        // when
        val result = oauthService.getAuthCodeUri(provider)

        // then
        assertThat(result).isEqualTo(expectedUri)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideOAuthData")
    fun `기존 OAuth 유저 로그인 성공`(socialType: SocialType, oAuthUserInfo: OAuthUserInfo) = runTest {
        // given
        val provider = socialType.name
        val code = "auth_code"
        val accessToken = "accessToken"

        val existingUser = User(
            id = 1L,
            email = "test@example.com",
            nickname = "nickname",
            authType = AuthType.OAUTH,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        coEvery { oauthPort.getUserInfo(socialType, code) } returns oAuthUserInfo
        coEvery { userFindPort.findByProviderAndProviderId(provider, oAuthUserInfo.providerId) } returns existingUser
        coEvery { userAuthPort.login(existingUser.id, existingUser.email) } returns accessToken

        // when
        val result = oauthService.login(provider, code)

        // then
        assertThat(result.id).isEqualTo(existingUser.id)
        assertThat(result.accessToken).isEqualTo(accessToken)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideOAuthData")
    fun `신규 OAuth 유저 회원가입 후 로그인 성공`(socialType: SocialType, oAuthUserInfo: OAuthUserInfo) = runTest {
        // given
        val provider = socialType.name
        val code = "auth_code"
        val accessToken = "accessToken"

        val newUser = User(
            id = 1L,
            email = "newUser@example.com",
            nickname = "newUser",
            authType = AuthType.OAUTH,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        coEvery { oauthPort.getUserInfo(socialType, code) } returns oAuthUserInfo
        coEvery { userFindPort.findByProviderAndProviderId(provider, oAuthUserInfo.providerId) } returns null
        coEvery { userSignUpPort.oauthSignup(oAuthUserInfo) } returns newUser
        coEvery { userAuthPort.login(newUser.id, newUser.email) } returns accessToken

        // when
        val result = oauthService.login(provider, code)

        // then
        assertThat(result.id).isEqualTo(newUser.id)
        assertThat(result.email).isEqualTo(newUser.email)
        assertThat(result.nickname).isEqualTo(newUser.nickname)
        assertThat(result.accessToken).isEqualTo(accessToken)
    }

}