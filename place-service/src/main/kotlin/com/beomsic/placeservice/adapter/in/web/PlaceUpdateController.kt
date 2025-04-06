package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.placeservice.application.port.`in`.command.PlaceUpdateCommand
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceUpdateUseCase
import com.beomsic.placeservice.domain.Category
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/place-service")
class PlaceUpdateController(
    private val placeUpdateUseCase: PlaceUpdateUseCase
) {

    @PutMapping("/{placeId}/content")
    suspend fun updateContent(
        @AuthToken authUser: AuthUser,
        @PathVariable("placeId") placeId: Long,
        @RequestBody request: PlaceUpdateContentRequest): ResponseEntity<PlaceDetailResponse> {
        val command = request.toUpdateCommand(placeId = placeId, authorId = authUser.id)
        val updatedPlace = placeUpdateUseCase.updateContent(command)
        return ResponseEntity.ok(PlaceDetailResponse(updatedPlace))
    }

    @PatchMapping("/{placeId}/image")
    suspend fun updateImage(
        @AuthToken authUser: AuthUser,
        @PathVariable("placeId") placeId: Long,
        @RequestPart("image") image: FilePart) {
        placeUpdateUseCase.updateImage(placeId = placeId, authorId = authUser.id, image = image)
    }

    private fun PlaceUpdateContentRequest.toUpdateCommand(placeId: Long, authorId: Long): PlaceUpdateCommand {
        return PlaceUpdateCommand(
            placeId = placeId,
            authorId = authorId,
            name = this.name,
            description = this.description,
            category = Category.fromValue(this.category),
            longitude = this.longitude,
            latitude = this.latitude
        )
    }

}