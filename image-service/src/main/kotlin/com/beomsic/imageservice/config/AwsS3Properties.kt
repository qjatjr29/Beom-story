package com.beomsic.imageservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class AwsS3Properties (
    @Value("\${aws.s3.accessKey}")
    val accessKey: String,
    @Value("\${aws.s3.secretKey}")
    val secretKey: String,
    @Value("\${aws.s3.region}")
    val region: String,
    @Value("\${aws.s3.bucket}")
    val bucket: String,
)