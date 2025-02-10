package com.beomsic.imageservice.application.service

import com.beomsic.imageservice.application.port.`in`.command.UploadImageCommand
import com.beomsic.imageservice.application.port.`in`.usecase.UploadUseCase
import com.beomsic.imageservice.application.port.out.UploadPort
import org.springframework.stereotype.Service

@Service
class UploadService(
    private val uploadPort: UploadPort
): UploadUseCase {

    override suspend fun uploadImage(command: UploadImageCommand): String {
        return uploadPort.uploadImage(command)
    }

}