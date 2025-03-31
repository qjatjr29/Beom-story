package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.command.UserNicknameUpdateCommand
import com.beomsic.userservice.application.port.`in`.command.UserPasswordUpdateCommand
import com.beomsic.userservice.application.port.`in`.usecase.UserUpdateUseCase
import com.beomsic.userservice.application.port.out.UserUpdatePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserUpdateService(
    private val userUpdatePort: UserUpdatePort,
    private val validationService: ValidationService,
): UserUpdateUseCase {

    @Transactional
    override suspend fun updateUserNickname(command: UserNicknameUpdateCommand) {
        userUpdatePort.updateNickname(command.userId, command.nickName)
    }

    @Transactional
    override suspend fun updateUserPassword(command: UserPasswordUpdateCommand) {
        validationService.validatePassword(command.password)
        userUpdatePort.updatePassword(command.userId, command.password)
    }
}