package com.beomsic.userservice.application.service.auth

import com.beomsic.userservice.application.port.`in`.usecase.OAuthUserCase
import com.beomsic.userservice.application.port.out.OAuthPort
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.port.out.UserLoginPort
import com.beomsic.userservice.application.port.out.UserSignUpPort
import com.beomsic.userservice.domain.oauth.SocialType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuthService(
    private val oauthPort: OAuthPort,
    private val userFindPort: UserFindPort,
    private val userLoginPort: UserLoginPort,
    private val userSignUpPort: UserSignUpPort
): OAuthUserCase {

    override suspend fun getAuthCodeUri(provider: String): String {
        return oauthPort.getAuthCodeUri(getSocialType(provider))
    }

    @Transactional
    override suspend fun login(provider: String, code: String): String {
        val userInfo = oauthPort.getUserInfo(getSocialType(provider), code)

        val user = userFindPort.findByProviderAndProviderId(provider, userInfo.providerId)
            ?: userSignUpPort.oauthSignup(userInfo)

        return user.id?.let { userId -> userLoginPort.login(userId, user.email) }
            ?: throw IllegalStateException("Unable to login user: Missing user ID")
    }

    private fun getSocialType(provider: String): SocialType = SocialType.fromProvider(provider)

}