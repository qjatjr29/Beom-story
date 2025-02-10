package com.beomsic.imageservice.application.port.`in`.usecase

import com.beomsic.imageservice.application.port.`in`.command.UploadImageCommand

interface UploadUseCase {
    suspend fun uploadImage(command: UploadImageCommand): String
}