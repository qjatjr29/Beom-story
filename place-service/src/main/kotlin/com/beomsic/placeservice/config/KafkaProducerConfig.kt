package com.beomsic.placeservice.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig(
    @Value("\${kafka.bootstrap-servers}") private val bootstrapServers: String,
//    private val kafkaProducerInterceptor: KafkaProducerInterceptor,
) {

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs = mapOf(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers
        )
        return KafkaAdmin(configs)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        val kafkaTemplate = KafkaTemplate(producerFactory())
//        kafkaTemplate.setProducerInterceptor(kafkaProducerInterceptor);
        return kafkaTemplate
    }
}

// Coroutine Extension for KafkaTemplate
fun <K : Any, V> KafkaTemplate<K, V>.sendMessageWithCallback(topic: String, key: K, value: V) {
    send(topic, key, value)
        .whenComplete { result, ex ->
            if (ex != null) {
                println("Kafka message delivery failed: ${ex.message}")
                // 예외 상황에 대한 재시도 또는 알람 처리
            } else {
                println("Message delivered to Kafka with offset: ${result.recordMetadata.offset()}")
            }
        }
}

