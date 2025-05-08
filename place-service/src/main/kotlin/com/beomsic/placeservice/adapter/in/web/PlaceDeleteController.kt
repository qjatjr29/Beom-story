package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.placeservice.application.port.`in`.usecase.PlaceDeleteUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/place-service")
class PlaceDeleteController(
    private val placeDeleteUseCase: PlaceDeleteUseCase
) {

    @DeleteMapping("/{placeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deletePlace(@AuthToken authUser: AuthUser, @PathVariable placeId: Long) {
        placeDeleteUseCase.deleteById(placeId, authUser.id)
    }

}