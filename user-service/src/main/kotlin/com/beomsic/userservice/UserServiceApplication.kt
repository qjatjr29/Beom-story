package com.beomsic.userservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcAuditing
class UserServiceApplication
fun main(args: Array<String>) {
	runApplication<UserServiceApplication>(*args)
}

