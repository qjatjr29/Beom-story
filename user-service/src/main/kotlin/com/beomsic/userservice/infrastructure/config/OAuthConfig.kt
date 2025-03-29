package com.beomsic.userservice.infrastructure.config

import com.beomsic.userservice.infrastructure.oauth.OAuth2Properties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OAuth2Properties::class)
class OAuthConfig {
}