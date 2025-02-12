package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.placeservice.application.port.`in`.usecase.PlaceFindUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/place-service")
class PlaceFindController(
    private val placeFindUseCase: PlaceFindUseCase
) {

    @GetMapping("/story/{storyId}")
    suspend fun findPlaces(@PathVariable("storyId") storyId: Long): ResponseEntity<List<PlaceDetailResponse>> {
        val places = placeFindUseCase.findAllByStoryId(storyId)
        val placeResponses = places.map { PlaceDetailResponse(it) }
        return ResponseEntity.ok(placeResponses)
    }

    @GetMapping("/{placeId}")
    suspend fun findPlace(@PathVariable("placeId") placeId: Long): ResponseEntity<PlaceDetailResponse> {
        val place = placeFindUseCase.findByPlaceId(placeId)
        return ResponseEntity.ok(PlaceDetailResponse(place))
    }


}