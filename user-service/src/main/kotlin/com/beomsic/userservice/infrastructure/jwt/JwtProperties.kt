package com.beomsic.userservice.infrastructure.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val issuer: String,
    val subject: String,
    val accessExpiresTime: Long,
    val refreshExpiresTime: Long,
    val secret: String,
)
