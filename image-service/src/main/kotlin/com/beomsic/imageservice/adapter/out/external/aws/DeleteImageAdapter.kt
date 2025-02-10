package com.beomsic.imageservice.adapter.out.external.aws

import com.beomsic.imageservice.application.port.out.DeletePort
import com.beomsic.imageservice.config.AwsS3Properties
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest

@Component
class DeleteImageAdapter(
    private val s3AsyncClient: S3AsyncClient,
    private val awsS3Properties: AwsS3Properties,
): DeletePort {

    private fun extractKeyFromUrl(url: String): String {
        val prefix = "https://${awsS3Properties.bucket}.s3.${awsS3Properties.region}.amazonaws.com/"
        return url.removePrefix(prefix)
    }

    override suspend fun deleteImage(imageUrl: String) {
        val key = extractKeyFromUrl(imageUrl)
        println(key)
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(awsS3Properties.bucket)
            .key(key)
            .build()
        s3AsyncClient.deleteObject(deleteObjectRequest).await()
//        try {
//            val key = extractKeyFromUrl(imageUrl)
//            val deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(awsS3Properties.bucket)
//                .key(key)
//                .build()
//            s3AsyncClient.deleteObject(deleteObjectRequest).await()
//        } catch (exception: Exception) {
//            // 이미지 삭제 실패 처리 (로깅 및 예외 처리)
////            log.error("이미지 삭제 실패: ${exception.message}")
//        }
    }
}