package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.common.annotation.AuthToken
import com.beomsic.common.model.AuthUser
import com.beomsic.userservice.adapter.`in`.web.dto.UserNicknameUpdateRequest
import com.beomsic.userservice.adapter.`in`.web.dto.UserPasswordUpdateRequest
import com.beomsic.userservice.application.port.`in`.command.UserNicknameUpdateCommand
import com.beomsic.userservice.application.port.`in`.command.UserPasswordUpdateCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserUpdateUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user-service")
class UserUpdateController(
    private val userUpdateUseCase: UserUpdateUseCase,
) {

    @PatchMapping("/nickname")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateUserNickname(@AuthToken authUser: AuthUser,
                                   @RequestBody request: UserNicknameUpdateRequest) {
        val command = UserNicknameUpdateCommand(authUser.id, request.nickname)
        userUpdateUseCase.updateUserNickname(command)
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun updateUserPassword(@AuthToken authUser: AuthUser,
                                   @RequestBody request: UserPasswordUpdateRequest) {
        val command = UserPasswordUpdateCommand(authUser.id, request.password)
        userUpdateUseCase.updateUserPassword(command)
    }

}