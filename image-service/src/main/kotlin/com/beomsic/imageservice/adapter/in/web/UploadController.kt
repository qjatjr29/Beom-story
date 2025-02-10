package com.beomsic.imageservice.adapter.`in`.web

import com.beomsic.imageservice.application.port.`in`.command.UploadImageCommand
import com.beomsic.imageservice.application.port.`in`.usecase.UploadUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/image-service")
class UploadController(
    private val uploadImageUseCase: UploadUseCase,
) {

    @PostMapping("/upload")
    suspend fun uploadImage(@RequestPart("image") image : FilePart): ResponseEntity<String> {
        val command = UploadImageCommand(image)
        val imageUrl = uploadImageUseCase.uploadImage(command)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(imageUrl)
    }

}