package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.adapter.`in`.web.dto.CheckDuplicateResponse
import com.beomsic.userservice.application.port.`in`.usecase.CheckDuplicateUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-service")
class CheckDuplicateController(
    private val checkDuplicateUseCase: CheckDuplicateUseCase
) {

    @PostMapping("/duplicate")
    suspend fun checkDuplicate(@RequestParam type: String,
                                    @RequestParam value: String): ResponseEntity<CheckDuplicateResponse> {
        val result = checkDuplicateUseCase.execute(type = type, value = value)

        return ResponseEntity.ok(CheckDuplicateResponse(result))
    }
}