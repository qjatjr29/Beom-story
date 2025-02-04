package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceCreateUseCase
import com.beomsic.placeservice.domain.Place
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/place-service")
class PlaceCreateController(
    private val placeCreateUseCase: PlaceCreateUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody request: PlaceCreateRequest): Place {
        val command = request.toCreateCommand()
        return placeCreateUseCase.execute(command)
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

