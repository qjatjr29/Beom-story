package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceCreateUseCase
import com.beomsic.placeservice.domain.Place
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/place-service")
class PlaceCreateController(
    private val placeCreateUseCase: PlaceCreateUseCase
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestPart request: PlaceCreateRequest,
                       @RequestPart(required = false) image: FilePart): ResponseEntity<Place> {
        val command = request.toCreateCommand()
        val place = placeCreateUseCase.execute(command, image)
        return ResponseEntity.status(HttpStatus.CREATED).body(place)
    }

    private fun PlaceCreateRequest.toCreateCommand(): PlaceCreateCommand {
        return PlaceCreateCommand(
            storyId = this.storyId,
            name = this.name,
            description = this.description,
            category = this.category,
            longitude = this.longitude,
            latitude = this.latitude
        )
    }
}

