package com.beomsic.userservice.infrastructure.oauth

data class OAuth2Property(
    val registration: RegistrationProperties,
    val provider: ProviderProperties
)

data class ProviderProperties(
    var authorizationUri: String = "",
    var tokenUri: String = "",
    var userInfoUri: String = "",
    var userNameAttribute: String = "",
)

data class RegistrationProperties(
    var clientId: String = "",
    var clientSecret: String = "",
    var clientAuthenticationMethod: String = "",
    var authorizationGrantType: String = "",
    var redirectUri: String = "",
    var scope: Set<String> = emptySet(),
)
