package com.beomsic.imageservice.adapter.out.external.aws

import com.beomsic.imageservice.application.port.`in`.command.UploadImageCommand
import com.beomsic.imageservice.application.port.out.UploadPort
import com.beomsic.imageservice.config.AwsS3Properties
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class UploadImageAdapter(
    private val s3AsyncClient: S3AsyncClient,
    private val awsS3Properties: AwsS3Properties,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${kafka.topic.upload-image}") val uploadImageTopic: String,
): UploadPort {

    override suspend fun uploadImage(command: UploadImageCommand): String {
        return uploadImage(command.image)
    }

    private suspend fun uploadImage(image: FilePart): String {
        val filename = URLEncoder.encode(image.filename(), StandardCharsets.UTF_8)
        val key = UUID.randomUUID()
        val bucket = awsS3Properties.bucket
        val dataBuffer = DataBufferUtils.join(image.content()).awaitSingle()
        val fileContent = ByteArray(dataBuffer.readableByteCount())
        val contentType = image.headers().contentType?.toString() ?: MediaType.APPLICATION_OCTET_STREAM.toString()

        dataBuffer.read(fileContent)
        DataBufferUtils.release(dataBuffer)

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key.toString())
            .metadata(mapOf("filename" to filename))
            .contentType(contentType)
            .build()

        s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(fileContent)).await()

        return "https://${bucket}.s3.${awsS3Properties.region}.amazonaws.com/$key"
    }
}