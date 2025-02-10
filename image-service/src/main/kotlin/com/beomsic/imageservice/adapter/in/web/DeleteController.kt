package com.beomsic.imageservice.adapter.`in`.web

import com.beomsic.imageservice.application.port.`in`.usecase.DeleteUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/image-service")
class DeleteController(
    private val deleteUseCase: DeleteUseCase
) {

    @DeleteMapping()
    suspend fun uploadImage(@RequestBody request: DeleteImageRequest) {
        deleteUseCase.deleteImage(imageUrl = request.imageUrl)
    }
}