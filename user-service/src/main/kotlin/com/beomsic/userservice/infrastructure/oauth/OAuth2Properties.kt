package com.beomsic.userservice.infrastructure.oauth

import com.beomsic.userservice.domain.oauth.SocialType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth")
data class OAuth2Properties (
    val registration: Map<SocialType, RegistrationProperties>,
    val provider: Map<SocialType, ProviderProperties>
) {

    fun getOauth2Properties(): Map<SocialType, OAuth2Property> {
        return SocialType.entries.associateWith { socialType ->
            OAuth2Property(
                registration = registration[socialType] ?: RegistrationProperties(),
                provider = provider[socialType] ?: ProviderProperties()
            )
        }
    }
}