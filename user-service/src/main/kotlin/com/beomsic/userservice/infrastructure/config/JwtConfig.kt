package com.beomsic.userservice.infrastructure.config

import com.beomsic.userservice.infrastructure.jwt.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig {
}