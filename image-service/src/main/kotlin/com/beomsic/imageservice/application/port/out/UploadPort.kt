package com.beomsic.imageservice.application.port.out

import com.beomsic.imageservice.application.port.`in`.command.UploadImageCommand

interface UploadPort {
    suspend fun uploadImage(command: UploadImageCommand): String
}