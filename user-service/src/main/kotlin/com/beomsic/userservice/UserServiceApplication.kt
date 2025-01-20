package com.beomsic.userservice

import com.beomsic.userservice.adapter.out.jwt.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(JwtProperties::class)
class UserServiceApplication
fun main(args: Array<String>) {
	runApplication<UserServiceApplication>(*args)
}

