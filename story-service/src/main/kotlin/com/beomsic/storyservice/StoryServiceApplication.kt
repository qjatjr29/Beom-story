package com.beomsic.storyservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class StoryServiceApplication
fun main(args: Array<String>) {
    runApplication<StoryServiceApplication>(*args)
}
