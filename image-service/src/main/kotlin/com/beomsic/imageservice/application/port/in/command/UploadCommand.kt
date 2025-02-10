package com.beomsic.imageservice.application.port.`in`.command

import org.springframework.http.codec.multipart.FilePart

data class UploadImageCommand(
    val image: FilePart
)
