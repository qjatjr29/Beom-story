package com.beomsic.placeservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class PlaceServiceApplication
fun main(args: Array<String>) {
    runApplication<PlaceServiceApplication>(*args)
}
