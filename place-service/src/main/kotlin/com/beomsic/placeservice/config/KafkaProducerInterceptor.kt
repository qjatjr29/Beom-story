//package com.beomsic.placeservice.config
//
//import org.apache.kafka.clients.producer.ProducerInterceptor
//import org.apache.kafka.clients.producer.ProducerRecord
//import org.apache.kafka.clients.producer.RecordMetadata
//import org.springframework.stereotype.Component
//import java.lang.Exception
//
//@Component
//class KafkaProducerInterceptor: ProducerInterceptor<String, Any> {
//    override fun onAcknowledgement(metadata: RecordMetadata?, exception: Exception?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onSend(record: ProducerRecord<String, Any>?): ProducerRecord<String, Any> {
//        TODO("Not yet implemented")
//    }
//
//    override fun configure(configs: MutableMap<String, *>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun close() {
//        TODO("Not yet implemented")
//    }
//
//}