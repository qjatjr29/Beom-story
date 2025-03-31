package com.beomsic.userservice.application.port.`in`.usecase

import com.beomsic.userservice.application.port.`in`.command.UserNicknameUpdateCommand
import com.beomsic.userservice.application.port.`in`.command.UserPasswordUpdateCommand

interface UserUpdateUseCase {
    suspend fun updateUserNickname(command: UserNicknameUpdateCommand)
    suspend fun updateUserPassword(command: UserPasswordUpdateCommand)
    suspend fun deleteUser(userId: Long, authUserId: Long)
}