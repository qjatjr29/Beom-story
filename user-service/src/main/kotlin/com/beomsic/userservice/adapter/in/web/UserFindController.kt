package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.userservice.application.port.`in`.usecase.UserFindUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-service")
class UserFindController (
    private val userFindUseCase : UserFindUseCase,
) {

    @GetMapping("/{id}")
    suspend fun getUserById(@PathVariable id: Long) : UserDetailResponse {
        return UserDetailResponse(userFindUseCase.findById(id))
    }

    @GetMapping("/me")
    suspend fun getMyUser(@AuthToken authUser: AuthUser): UserDetailResponse {
        return UserDetailResponse(userFindUseCase.findById(authUser.id))
    }

}