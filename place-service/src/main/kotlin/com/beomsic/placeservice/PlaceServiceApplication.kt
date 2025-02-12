package com.beomsic.placeservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcAuditing
class PlaceServiceApplication
fun main(args: Array<String>) {
    runApplication<PlaceServiceApplication>(*args)
}
